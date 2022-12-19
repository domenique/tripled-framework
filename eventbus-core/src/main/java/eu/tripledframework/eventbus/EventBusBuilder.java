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

package eu.tripledframework.eventbus;

import eu.tripledframework.eventbus.internal.domain.AsynchronousEventBus;
import eu.tripledframework.eventbus.internal.domain.DefaultEventBusBuilder;
import eu.tripledframework.eventbus.internal.domain.InterceptorChainFactory;
import eu.tripledframework.eventbus.internal.domain.InvokerFactory;
import eu.tripledframework.eventbus.internal.domain.InvokerRepository;
import eu.tripledframework.eventbus.internal.domain.SynchronousEventBus;
import eu.tripledframework.eventbus.internal.domain.UnitOfWorkFactory;

import java.util.List;
import java.util.concurrent.Executor;

public interface EventBusBuilder {

  static EventBusBuilder newBuilder() {
    return new DefaultEventBusBuilder();
  }

  EventBusBuilder withInvokerRepository(InvokerRepository invokerRepository);

  EventBusBuilder withInvokerInterceptorChainFactory(InterceptorChainFactory invokerInterceptorChainFactory);

  EventBusBuilder withInvokerInterceptors(List<EventBusInterceptor> invokers);

  EventBusBuilder withReceiverInterceptorChainFactory(InterceptorChainFactory receiverInterceptorChainFactory);

  EventBusBuilder withReceiverInterceptors(List<EventBusInterceptor> interceptors);

  EventBusBuilder withUnitOfWorkFactory(UnitOfWorkFactory unitOfWorkFactory);

  EventBusBuilder withEventHandlerInvokerFactories(List<InvokerFactory> eventHandlerInvokerFactories);

  EventBusBuilder withExecutor(Executor executor);

  SynchronousEventBus buildSynchronousEventBus();

  AsynchronousEventBus buildASynchronousEventBus();
}
