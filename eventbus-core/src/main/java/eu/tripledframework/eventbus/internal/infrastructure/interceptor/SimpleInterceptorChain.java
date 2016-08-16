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
package eu.tripledframework.eventbus.internal.infrastructure.interceptor;

import java.util.Iterator;

import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.internal.domain.InterceptorChain;
import eu.tripledframework.eventbus.internal.domain.Invoker;
import eu.tripledframework.eventbus.internal.domain.UnitOfWork;

public class SimpleInterceptorChain<ReturnType> implements InterceptorChain<ReturnType> {

  private final Object event;
  private final UnitOfWork unitOfWork;
  private final Iterator<EventBusInterceptor> interceptors;
  private final Iterator<Invoker> invokers;

  public SimpleInterceptorChain(Object event, UnitOfWork unitOfWork, Iterator<Invoker> invokers,
                                Iterator<EventBusInterceptor> interceptors) {
    this.event = event;
    this.unitOfWork = unitOfWork;
    this.invokers = invokers;
    this.interceptors = interceptors;
  }

  @Override
  public ReturnType proceed() {
    if (interceptors.hasNext()) {
      EventBusInterceptor nextInterceptor = interceptors.next();
      return nextInterceptor.intercept(this, event, unitOfWork);
    } else {
      return invokeEventHandlers();
    }
  }

  @SuppressWarnings("unchecked")
  private ReturnType invokeEventHandlers() {
    ReturnType response = null;
    while (invokers.hasNext()) {
      Invoker current = invokers.next();
      if (current.hasReturnType()) {
        response = (ReturnType) current.invoke(event);
      } else {
        current.invoke(event);
      }
    }

    return response;
  }
}
