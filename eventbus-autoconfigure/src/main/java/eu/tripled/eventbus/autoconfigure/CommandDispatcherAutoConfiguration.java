package eu.tripled.eventbus.autoconfigure;

import eu.tripled.eventbus.EventPublisher;
import eu.tripled.eventbus.EventBusInterceptor;
import eu.tripled.eventbus.asynchronous.AsynchronousEventBus;
import eu.tripled.eventbus.synchronous.SynchronousEventBus;
import eu.tripled.eventbus.interceptor.LoggingEventBusInterceptor;
import eu.tripled.eventbus.interceptor.ValidatingEventBusInterceptor;
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
  @ConditionalOnMissingBean(EventPublisher.class)
  @ConditionalOnProperty(value = "eu.tripled.eventbus.useAsync", havingValue = "false", matchIfMissing = true)
  public SynchronousEventBus synchronousCommandDispatcher() {
    List<EventBusInterceptor> interceptors =
        Lists.newArrayList(new LoggingEventBusInterceptor(), new ValidatingEventBusInterceptor());

    return new SynchronousEventBus(interceptors);
  }

  @Bean
  @ConditionalOnMissingBean(EventPublisher.class)
  @ConditionalOnProperty(value = "eu.tripled.eventbus.useAsync", havingValue = "true")
  public AsynchronousEventBus asynchronousCommandDispatcher(Executor executor) {
    List<EventBusInterceptor> interceptors =
        Lists.newArrayList(new LoggingEventBusInterceptor(), new ValidatingEventBusInterceptor());

    return new AsynchronousEventBus(interceptors, executor);
  }

  @Bean
  @ConditionalOnMissingBean(Executor.class)
  @ConditionalOnProperty(value = "eu.tripled.eventbus.useAsync", havingValue = "true")
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executorService = new ThreadPoolTaskExecutor();
    executorService.setCorePoolSize(5);
    executorService.setMaxPoolSize(10);

    executorService.afterPropertiesSet();
    return executorService;

  }


}
