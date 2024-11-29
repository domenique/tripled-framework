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

package eu.tripledframework.demo;

import eu.tripledframework.demo.security.SecurityContextInitializiationInterceptor;
import eu.tripledframework.demo.security.SecurityContextPropagationInterceptor;
import eu.tripledframework.eventbus.EventBusBuilder;
import eu.tripledframework.eventbus.autoconfigure.EnableEventHandlerSupport;
import eu.tripledframework.eventbus.internal.domain.AsynchronousEventBus;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.LoggingEventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.ValidatingEventBusInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@EnableEventHandlerSupport(basePackage = "eu.tripledframework.demo")
public class EventBusConfiguration {

  @Bean
  public AsynchronousEventBus asynchronousEventBus(LocalValidatorFactoryBean validatorFactoryBean) {
    return EventBusBuilder.newBuilder()
            .withReceiverInterceptors(List.of(new SecurityContextPropagationInterceptor()))
            .withInvokerInterceptors(List.of(new LoggingEventBusInterceptor(),
                    new SecurityContextInitializiationInterceptor(),
                    new ValidatingEventBusInterceptor(validatorFactoryBean.getValidator())))
            .withExecutor(taskExecutor())
            .buildASynchronousEventBus();
  }

  @Bean
  public Executor taskExecutor() {
    var executorService = new ThreadPoolTaskExecutor();
    executorService.setCorePoolSize(5);
    executorService.setMaxPoolSize(10);

    executorService.afterPropertiesSet();
    return executorService;
  }
}
