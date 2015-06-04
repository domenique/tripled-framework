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
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;
import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@EnableConfigurationProperties(EventBusProperties.class)
public class EventBusAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(EventPublisher.class)
  @ConditionalOnProperty(value = "eu.tripled.eventbus.useAsync", havingValue = "false", matchIfMissing = true)
  public SynchronousEventBus synchronousCommandDispatcher(Validator validator) {
    List<EventBusInterceptor> interceptors =
        Lists.newArrayList(new LoggingEventBusInterceptor(), new ValidatingEventBusInterceptor(validator));

    return new SynchronousEventBus(interceptors);
  }

  @Bean
  @ConditionalOnMissingBean(EventPublisher.class)
  @ConditionalOnProperty(value = "eu.tripled.eventbus.useAsync", havingValue = "true")
  public AsynchronousEventBus asynchronousCommandDispatcher(Executor executor, LocalValidatorFactoryBean validator) {
    List<EventBusInterceptor> interceptors =
        Lists.newArrayList(new LoggingEventBusInterceptor(), new ValidatingEventBusInterceptor(validator.getValidator()));

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

  @Bean
  @ConditionalOnMissingBean(LocalValidatorFactoryBean.class)
  public LocalValidatorFactoryBean localValidatorFactoryBean() {
    return new LocalValidatorFactoryBean();
  }


}
