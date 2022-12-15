/*
 * Copyright 2022 TripleD framework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.tripledframework.eventbus.autoconfigure;

import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.internal.domain.AsynchronousEventBus;
import eu.tripledframework.eventbus.internal.domain.SynchronousEventBus;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.LoggingEventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.SimpleInterceptorChainFactory;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.ValidatingEventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InMemoryInvokerRepository;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.SimpleInvokerFactory;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.DefaultUnitOfWorkFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@EnableConfigurationProperties(EventBusProperties.class)
public class EventBusAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean({SynchronousEventBus.class, AsynchronousEventBus.class})
  @ConditionalOnProperty(value = "eu.tripledframework.eventbus.mode", havingValue = "sync")
  public SynchronousEventBus synchronousEventBus() {
    List<EventBusInterceptor> interceptors =
        Arrays.asList(new LoggingEventBusInterceptor(),
            new ValidatingEventBusInterceptor(localValidatorFactoryBean().getValidator()));

    return new SynchronousEventBus(new InMemoryInvokerRepository(), new SimpleInterceptorChainFactory(interceptors), Collections
        .singletonList(new SimpleInvokerFactory()), new DefaultUnitOfWorkFactory());
  }

  @Bean
  @ConditionalOnMissingBean({SynchronousEventBus.class, AsynchronousEventBus.class})
  @ConditionalOnProperty(value = "eu.tripledframework.eventbus.mode", matchIfMissing = true, havingValue = "async")
  public AsynchronousEventBus asynchronousEventBus() {
    List<EventBusInterceptor> interceptors =
        Arrays.asList(new LoggingEventBusInterceptor(),
            new ValidatingEventBusInterceptor(localValidatorFactoryBean().getValidator()));

    return new AsynchronousEventBus(new InMemoryInvokerRepository(), new SimpleInterceptorChainFactory(interceptors), Collections
        .singletonList(new SimpleInvokerFactory()), new DefaultUnitOfWorkFactory(), taskExecutor());
  }

//  @Bean
//  @ConditionalOnProperty(value = "eu.tripledframework.eventbus.mode", matchIfMissing = true, havingValue = "async")
  private Executor taskExecutor() {
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
