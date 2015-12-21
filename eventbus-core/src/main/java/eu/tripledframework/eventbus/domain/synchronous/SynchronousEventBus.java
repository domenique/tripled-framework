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
package eu.tripledframework.eventbus.domain.synchronous;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.EventCallback;
import eu.tripledframework.eventbus.domain.EventPublisher;
import eu.tripledframework.eventbus.domain.EventSubscriber;
import eu.tripledframework.eventbus.domain.callback.FutureEventCallback;
import eu.tripledframework.eventbus.domain.dispatcher.CommandDispatcher;
import eu.tripledframework.eventbus.domain.dispatcher.EventDispatcher;
import eu.tripledframework.eventbus.domain.interceptor.InterceptorChainFactory;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvoker;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvokerFactory;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvokerRepository;
import eu.tripledframework.eventbus.domain.invoker.InstanceEventHandlerInvokerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * Synchronous implementation of the CommandDispatcher.
 */
public class SynchronousEventBus implements eu.tripledframework.eventbus.domain.CommandDispatcher, EventPublisher, EventSubscriber {

  private final Logger logger = LoggerFactory.getLogger(SynchronousEventBus.class);

  private final EventHandlerInvokerRepository invokerRepository;
  private final InterceptorChainFactory interceptorChainFactory;
  private List<EventHandlerInvokerFactory> eventHandlerInvokerFactories;

  // constructors

  public SynchronousEventBus() {
    this.invokerRepository = new EventHandlerInvokerRepository();
    this.interceptorChainFactory = new InterceptorChainFactory();
    this.eventHandlerInvokerFactories = Collections.singletonList(new InstanceEventHandlerInvokerFactory());
  }

  public SynchronousEventBus(List<EventBusInterceptor> interceptors) {
    this.invokerRepository = new EventHandlerInvokerRepository();
    this.interceptorChainFactory = new InterceptorChainFactory(interceptors);
    this.eventHandlerInvokerFactories = Collections.singletonList(new InstanceEventHandlerInvokerFactory());
  }

  // subscribe methods

  @Override
  public void subscribe(Object eventHandler) {
    eventHandlerInvokerFactories.stream().filter(cur -> cur.supports(eventHandler)).findFirst().ifPresent(f -> f.create(eventHandler).forEach(this::subscribeInternal));
  }

  protected void subscribeInternal(EventHandlerInvoker eventHandler) {
    getLogger().info("Adding Event subscription for {}", eventHandler.toString());
    invokerRepository.addEventHandlerInvoker(eventHandler);
  }

  // dispatch methods

  @Override
  public <ReturnType> Future<ReturnType> dispatch(Object command) {
    FutureEventCallback<ReturnType> future = new FutureEventCallback<>();
    dispatch(command, future);

    return future;
  }

  @Override
  public <ReturnType> void dispatch(Object command, EventCallback<ReturnType> callback) {
    Objects.requireNonNull(command, "The command cannot be null.");
    Objects.requireNonNull(callback, "The callback cannot be null.");
    getLogger().debug("Received an command for publication: {}", command);

    dispatchInternal(command, callback);

    getLogger().debug("Dispatched command {}", command);
  }

  protected <ReturnType> void dispatchInternal(Object event, EventCallback<ReturnType> callback) {
    new CommandDispatcher<>(event, callback, invokerRepository, interceptorChainFactory).dispatch();
  }

  // publish methods

  @Override
  public void publish(Object event) {
    Objects.requireNonNull(event, "The event should not be null.");
    getLogger().debug("Received an event to publish. {}", event);

    // TODO: Call appropriate event dispatcher which does not work with callbacks, but can invoke multiple handlers.
    new EventDispatcher<Void>(event, invokerRepository, interceptorChainFactory).dispatch();

    getLogger().debug("Published event {}", event);
  }

  protected Logger getLogger() {
    return logger;
  }

  // optional setters to override behaviour.

  public void setEventHandlerInvokerFactory(List<EventHandlerInvokerFactory> eventHandlerInvokerFactories) {
    if (eventHandlerInvokerFactories == null || eventHandlerInvokerFactories.isEmpty()) {
      throw new IllegalArgumentException("At least one eventHandlerInvokerFactory should be configured.");
    }
    this.eventHandlerInvokerFactories = eventHandlerInvokerFactories;
  }
}

