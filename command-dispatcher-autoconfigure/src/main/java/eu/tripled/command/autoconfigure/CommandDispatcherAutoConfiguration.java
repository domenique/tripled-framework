package eu.tripled.command.autoconfigure;

import eu.tripled.command.CommandDispatcher;
import eu.tripled.command.CommandDispatcherInterceptor;
import eu.tripled.command.dispatcher.AsynchronousCommandDispatcher;
import eu.tripled.command.dispatcher.SynchronousCommandDispatcher;
import eu.tripled.command.interceptor.LoggingCommandDispatcherInterceptor;
import eu.tripled.command.interceptor.ValidatingCommandDispatcherInterceptor;
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
