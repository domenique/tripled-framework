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
package eu.tripledframework.eventbus.domain.dispatcher;

import eu.tripledframework.eventbus.domain.interceptor.InterceptorChainFactory;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvoker;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvokerRepository;

import java.util.Iterator;
import java.util.List;

public class EventDispatcher<ReturnType> implements Dispatcher {

  private final EventHandlerInvokerRepository invokerRepository;
  private final InterceptorChainFactory interceptorChainFactory;
  private final Object event;

  public EventDispatcher(Object event, EventHandlerInvokerRepository invokerRepository, InterceptorChainFactory interceptorChainFactory) {
    this.event = event;
    this.invokerRepository = invokerRepository;
    this.interceptorChainFactory = interceptorChainFactory;
  }

  @Override
  public void dispatch() {
    List<EventHandlerInvoker> invokers = invokerRepository.findAllByEventType(event.getClass());
    assertInvokerIsFound(invokers);

    executeChain(event, invokers.iterator());
  }

  private void executeChain(Object event, Iterator<EventHandlerInvoker> eventHandlerInvoker) {
    interceptorChainFactory.<ReturnType>createChain(event, eventHandlerInvoker).proceed();
  }

  private void assertInvokerIsFound(List<EventHandlerInvoker> invokers) {
    if (invokers == null || invokers.isEmpty()) {
      throw new HandlerNotFoundException(String.format("Could not find an event handler for %s", event));
    }
  }
}
