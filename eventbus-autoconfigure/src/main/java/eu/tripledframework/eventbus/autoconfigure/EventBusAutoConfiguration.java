package eu.tripledframework.eventbus.autoconfigure;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.EventPublisher;
import eu.tripledframework.eventbus.domain.asynchronous.AsynchronousEventBus;
import eu.tripledframework.eventbus.domain.interceptor.LoggingEventBusInterceptor;
import eu.tripledframework.eventbus.domain.interceptor.ValidatingEventBusInterceptor;
import eu.tripledframework.eventbus.domain.synchronous.SynchronousEventBus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@EnableConfigurationProperties(EventBusProperties.class)
public class EventBusAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(EventPublisher.class)
  @ConditionalOnProperty(value = "eu.tripledframework.eventbus.useAsync", havingValue = "false", matchIfMissing = true)
  public SynchronousEventBus synchronousCommandDispatcher(Validator validator) {
    List<EventBusInterceptor> interceptors =
        Arrays.asList(new LoggingEventBusInterceptor(), new ValidatingEventBusInterceptor(validator));

    return new SynchronousEventBus(interceptors);
  }

  @Bean
  @ConditionalOnMissingBean(EventPublisher.class)
  @ConditionalOnProperty(value = "eu.tripledframework.eventbus.useAsync", havingValue = "true")
  public AsynchronousEventBus asynchronousCommandDispatcher(Executor executor, LocalValidatorFactoryBean validator) {
    List<EventBusInterceptor> interceptors =
        Arrays.asList(new LoggingEventBusInterceptor(), new ValidatingEventBusInterceptor(validator.getValidator()));

    return new AsynchronousEventBus(interceptors, executor);
  }

  @Bean
  @ConditionalOnMissingBean(Executor.class)
  @ConditionalOnProperty(value = "eu.tripledframework.eventbus.useAsync", havingValue = "true")
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
