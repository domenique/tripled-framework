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

package eu.tripledframework.eventbus.domain.dispatcher;

import java.util.Iterator;
import java.util.List;

import eu.tripledframework.eventbus.domain.EventCallback;
import eu.tripledframework.eventbus.domain.InterceptorChain;
import eu.tripledframework.eventbus.domain.interceptor.EventHandlerInterceptorChainFactory;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvoker;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvokerRepository;

/**
 * The event dispatcher is responsible for dispatching an event to all registered handlers.
 * <p>
 * There can be at most one event handler with a returnType specified for any given event type.
 * If an event handler exists with a returnType, this one is executed first, the order of other event handlers is undefined and should not be
 * relied upon. If the any of the event handlers fail, the others are still invoked.
 *
 * @param <ReturnType> The return type of the commandHandler it dispatches to.
 */
public class EventDispatcher<ReturnType> {

  private final EventHandlerInvokerRepository invokerRepository;
  private final EventHandlerInterceptorChainFactory eventHandlerInterceptorChainFactory;
  private final Object event;
  private final EventCallback<ReturnType> callback;

  public EventDispatcher(Object event, EventCallback<ReturnType> callback,
                         EventHandlerInvokerRepository invokerRepository, EventHandlerInterceptorChainFactory eventHandlerInterceptorChainFactory) {
    this.event = event;
    this.callback = callback;
    this.invokerRepository = invokerRepository;
    this.eventHandlerInterceptorChainFactory = eventHandlerInterceptorChainFactory;
  }

  public void dispatch() {
    List<EventHandlerInvoker> invokers = invokerRepository.findAllByEventType(event.getClass());
    assertInvokerIsFound(invokers);

    ReturnType response = null;
    RuntimeException thrownException = null;
      try {
        response = executeChain(event, invokers.iterator());
      } catch (RuntimeException exception) {
        thrownException = exception;
      }
    invokeAppropriateCallbackMethod(response, thrownException);
  }

  private ReturnType executeChain(Object event, Iterator<EventHandlerInvoker> eventHandlerInvoker) {
    InterceptorChain<ReturnType> chain = eventHandlerInterceptorChainFactory.createChain(event, eventHandlerInvoker);
    return chain.proceed();
  }

  private void invokeAppropriateCallbackMethod(ReturnType response, RuntimeException thrownException) {
    if (thrownException != null) {
      callback.onFailure(thrownException);
    } else {
      callback.onSuccess(response);
    }
  }

  private void assertInvokerIsFound(List<EventHandlerInvoker> invokersWithReturnType) {
    if (invokersWithReturnType == null || invokersWithReturnType.isEmpty()) {
      throw new EventHandlerNotFoundException(String.format("Could not find an event handler for %s", event));
    }
  }
}
