/*
 * Copyright 2015 TripleD framework.
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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import eu.tripledframework.eventbus.domain.EventSubscriber;
import eu.tripledframework.eventbus.domain.annotation.EventHandler;

public class EventHandlerRegistrationBeanPostProcessor implements BeanPostProcessor {

  private EventSubscriber eventSubscriber;

  @Autowired
  public EventHandlerRegistrationBeanPostProcessor(EventSubscriber eventSubscriber) {
    this.eventSubscriber = eventSubscriber;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    EventHandler annotation = bean.getClass().getAnnotation(EventHandler.class);
    if (annotation != null) {
      eventSubscriber.subscribe(bean);
    }

    return bean;
  }
}
