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

import java.util.List;

abstract class AbstractEventBusTest {

  SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> interceptors) {
    return EventBusBuilder.newBuilder()
            .withInvokerInterceptors(interceptors)
            .buildSynchronousEventBus();
  }

  SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> invokerInterceptors, List<EventBusInterceptor> receiverInterceptors) {
    return EventBusBuilder.newBuilder()
            .withInvokerInterceptors(invokerInterceptors)
            .withReceiverInterceptors(receiverInterceptors)
            .buildSynchronousEventBus();
  }

  SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> interceptors, UnitOfWorkFactory unitOfWorkFactory) {
    return EventBusBuilder.newBuilder()
            .withInvokerInterceptors(interceptors)
            .withUnitOfWorkFactory(unitOfWorkFactory)
            .buildSynchronousEventBus();
  }

  SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> interceptors, List<EventBusInterceptor> receiverInterceptors,
                                                List<InvokerFactory> invokerFactories) {
    return EventBusBuilder.newBuilder()
            .withInvokerInterceptors(interceptors)
            .withReceiverInterceptors(interceptors)
            .withEventHandlerInvokerFactories(invokerFactories)
            .buildSynchronousEventBus();

  }
}
