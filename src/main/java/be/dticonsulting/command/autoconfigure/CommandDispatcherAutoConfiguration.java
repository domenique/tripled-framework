package be.dticonsulting.command.autoconfigure;

import be.dticonsulting.command.CommandDispatcher;
import be.dticonsulting.command.CommandDispatcherInterceptor;
import be.dticonsulting.command.dispatcher.AsynchronousCommandDispatcher;
import be.dticonsulting.command.dispatcher.SynchronousCommandDispatcher;
import be.dticonsulting.command.interceptor.LoggingCommandDispatcherInterceptor;
import be.dticonsulting.command.interceptor.ValidatingCommandDispatcherInterceptor;
import com.google.common.collect.Lists;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@EnableConfigurationProperties(CommandDispatcherProperties.class)
public class CommandDispatcherAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(CommandDispatcher.class)
  @ConditionalOnProperty(value = "command-dispatcher.useAsync", havingValue = "false", matchIfMissing = true)
  public CommandDispatcher synchronousCommandDispatcher() {
    List<CommandDispatcherInterceptor> interceptors =
        Lists.newArrayList(new LoggingCommandDispatcherInterceptor(), new ValidatingCommandDispatcherInterceptor());

    return new SynchronousCommandDispatcher(interceptors);
  }

  @Bean
  @ConditionalOnMissingBean(CommandDispatcher.class)
  @ConditionalOnProperty(value = "command-dispatcher.useAsync", havingValue = "true")
  public CommandDispatcher asynchronousCommandDispatcher(Executor executor) {
    List<CommandDispatcherInterceptor> interceptors =
        Lists.newArrayList(new LoggingCommandDispatcherInterceptor(), new ValidatingCommandDispatcherInterceptor());

    return new AsynchronousCommandDispatcher(interceptors, executor);
  }

  @Bean
  @ConditionalOnMissingBean(Executor.class)
  @ConditionalOnProperty(value = "command-dispatcher.useAsync", havingValue = "true")
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executorService = new ThreadPoolTaskExecutor();
    executorService.setCorePoolSize(5);
    executorService.setMaxPoolSize(10);

    executorService.afterPropertiesSet();
    return executorService;

  }


}
