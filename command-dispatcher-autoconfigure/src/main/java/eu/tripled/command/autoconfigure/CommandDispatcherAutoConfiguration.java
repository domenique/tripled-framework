package eu.tripled.command.autoconfigure;

import eu.tripled.command.Publisher;
import eu.tripled.command.EventBusInterceptor;
import eu.tripled.command.dispatcher.AsynchronousEventBus;
import eu.tripled.command.dispatcher.SynchronousEventBus;
import eu.tripled.command.interceptor.LoggingEventBusInterceptor;
import eu.tripled.command.interceptor.ValidatingEventBusInterceptor;
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
  @ConditionalOnMissingBean(Publisher.class)
  @ConditionalOnProperty(value = "commandMessage-dispatcher.useAsync", havingValue = "false", matchIfMissing = true)
  public Publisher synchronousCommandDispatcher() {
    List<EventBusInterceptor> interceptors =
        Lists.newArrayList(new LoggingEventBusInterceptor(), new ValidatingEventBusInterceptor());

    return new SynchronousEventBus(interceptors);
  }

  @Bean
  @ConditionalOnMissingBean(Publisher.class)
  @ConditionalOnProperty(value = "commandMessage-dispatcher.useAsync", havingValue = "true")
  public Publisher asynchronousCommandDispatcher(Executor executor) {
    List<EventBusInterceptor> interceptors =
        Lists.newArrayList(new LoggingEventBusInterceptor(), new ValidatingEventBusInterceptor());

    return new AsynchronousEventBus(interceptors, executor);
  }

  @Bean
  @ConditionalOnMissingBean(Executor.class)
  @ConditionalOnProperty(value = "commandMessage-dispatcher.useAsync", havingValue = "true")
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executorService = new ThreadPoolTaskExecutor();
    executorService.setCorePoolSize(5);
    executorService.setMaxPoolSize(10);

    executorService.afterPropertiesSet();
    return executorService;

  }


}
