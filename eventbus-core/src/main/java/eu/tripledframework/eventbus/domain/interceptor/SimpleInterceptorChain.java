/*
 * Copyright 2015 TripleD, DTI-Consulting.
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

package eu.tripledframework.eventbus.domain.interceptor;

import java.util.Iterator;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.InterceptorChain;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvoker;

public class SimpleInterceptorChain<ReturnType> implements InterceptorChain<ReturnType> {

  private final Object event;
  private final Iterator<EventBusInterceptor> interceptors;
  private final Iterator<EventHandlerInvoker> invokers;

  public SimpleInterceptorChain(Object event, Iterator<EventHandlerInvoker> invokers,
                                Iterator<EventBusInterceptor> interceptors) {
    this.event = event;
    this.invokers = invokers;
    this.interceptors = interceptors;
  }

  @Override
  public ReturnType proceed() {
    if (interceptors.hasNext()) {
      EventBusInterceptor nextInterceptor = interceptors.next();
      return nextInterceptor.intercept(this, event);
    } else {
      return invokeEventHandlers();
    }
  }

  @SuppressWarnings("unchecked")
  private ReturnType invokeEventHandlers() {
    ReturnType response = null;
    while (invokers.hasNext()) {
      EventHandlerInvoker current = invokers.next();
      if (current.hasReturnType()) {
        response = (ReturnType) current.invoke(event);
      } else {
        current.invoke(event);
      }
    }

    return response;
  }
}
