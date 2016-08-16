package eu.tripledframework.demo;

import eu.tripledframework.demo.security.SpringSecurityAwareUnitOfWorkFactory;
import eu.tripledframework.demo.security.SpringSecurityInitializationEventBusInterceptor;
import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.autoconfigure.EnableEventHandlerSupport;
import eu.tripledframework.eventbus.internal.domain.AsynchronousEventBus;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.LoggingEventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.SimpleInterceptorChainFactory;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.ValidatingEventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InMemoryInvokerRepository;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.SimpleInvokerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@EnableEventHandlerSupport(basePackage = "eu.tripledframework.demo")
public class EventBusConfiguration {

  @Bean
  public AsynchronousEventBus asynchronousEventBus(LocalValidatorFactoryBean validatorFactoryBean) {
    List<EventBusInterceptor> interceptors = Arrays
        .asList(new LoggingEventBusInterceptor(), new SpringSecurityInitializationEventBusInterceptor(),
            new ValidatingEventBusInterceptor(validatorFactoryBean.getValidator()));

    return new AsynchronousEventBus(
        new InMemoryInvokerRepository(),
        new SimpleInterceptorChainFactory(interceptors),
        Collections.singletonList(new SimpleInvokerFactory()),
        new SpringSecurityAwareUnitOfWorkFactory(),
        taskExecutor());
  }

  private Executor taskExecutor() {
    ThreadPoolTaskExecutor executorService = new ThreadPoolTaskExecutor();
    executorService.setCorePoolSize(5);
    executorService.setMaxPoolSize(10);

    executorService.afterPropertiesSet();
    return executorService;
  }
}
