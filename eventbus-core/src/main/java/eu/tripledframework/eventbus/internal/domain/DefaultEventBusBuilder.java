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

package eu.tripledframework.eventbus.internal.domain;

import eu.tripledframework.eventbus.EventBusBuilder;
import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.SimpleInterceptorChainFactory;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InMemoryInvokerRepository;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.SimpleInvokerFactory;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.DefaultUnitOfWorkFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class DefaultEventBusBuilder implements EventBusBuilder {
  Executor executor;
  InvokerRepository invokerRepository;
  InterceptorChainFactory invokerInterceptorChainFactory;
  InterceptorChainFactory receiverInterceptorChainFactory;
  UnitOfWorkFactory unitOfWorkFactory;
  List<InvokerFactory> eventHandlerInvokerFactories;

  public DefaultEventBusBuilder() {
    this.invokerRepository = new InMemoryInvokerRepository();
    this.invokerInterceptorChainFactory = new SimpleInterceptorChainFactory();
    this.receiverInterceptorChainFactory = new SimpleInterceptorChainFactory();
    this.unitOfWorkFactory = new DefaultUnitOfWorkFactory();
    this.eventHandlerInvokerFactories = Collections.singletonList(new SimpleInvokerFactory());
    this.executor = Executors.newCachedThreadPool();
  }

  @Override
  public EventBusBuilder withInvokerRepository(InvokerRepository invokerRepository) {
    this.invokerRepository = invokerRepository;
    return this;
  }

  @Override
  public EventBusBuilder withInvokerInterceptorChainFactory(InterceptorChainFactory invokerInterceptorChainFactory) {
    this.invokerInterceptorChainFactory = invokerInterceptorChainFactory;
    return this;
  }

  @Override
  public EventBusBuilder withInvokerInterceptors(List<EventBusInterceptor> invokers) {
    this.invokerInterceptorChainFactory = new SimpleInterceptorChainFactory(invokers);
    return this;
  }

  @Override
  public EventBusBuilder withReceiverInterceptorChainFactory(InterceptorChainFactory receiverInterceptorChainFactory) {
    this.receiverInterceptorChainFactory = receiverInterceptorChainFactory;
    return this;
  }

  @Override
  public EventBusBuilder withReceiverInterceptors(List<EventBusInterceptor> interceptors) {
    this.receiverInterceptorChainFactory = new SimpleInterceptorChainFactory(interceptors);
    return this;
  }

  @Override
  public EventBusBuilder withUnitOfWorkFactory(UnitOfWorkFactory unitOfWorkFactory) {
    this.unitOfWorkFactory = unitOfWorkFactory;
    return this;
  }

  @Override
  public EventBusBuilder withEventHandlerInvokerFactories(List<InvokerFactory> eventHandlerInvokerFactories) {
    this.eventHandlerInvokerFactories = eventHandlerInvokerFactories;
    return this;
  }

  @Override
  public EventBusBuilder withExecutor(Executor executor) {
    this.executor = executor;
    return this;
  }

  @Override
  public SynchronousEventBus buildSynchronousEventBus() {
    return new SynchronousEventBus(this);
  }

  @Override
  public AsynchronousEventBus buildASynchronousEventBus() {
    return new AsynchronousEventBus(this);
  }
}
