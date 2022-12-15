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

import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.SimpleInterceptorChainFactory;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InMemoryInvokerRepository;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.SimpleInvokerFactory;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.DefaultUnitOfWorkFactory;

import java.util.Collections;
import java.util.List;

abstract class AbstractEventBusTest {

  SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> interceptors) {
    return new SynchronousEventBus(new InMemoryInvokerRepository(), new SimpleInterceptorChainFactory(interceptors), Collections
        .singletonList(new SimpleInvokerFactory()), new DefaultUnitOfWorkFactory());
  }

  SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> interceptors, UnitOfWorkFactory unitOfWorkFactory) {
    return new SynchronousEventBus(new InMemoryInvokerRepository(), new SimpleInterceptorChainFactory(interceptors), Collections
        .singletonList(new SimpleInvokerFactory()), unitOfWorkFactory);
  }

  SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> interceptors, List<InvokerFactory> invokerFactories) {
    return new SynchronousEventBus(new InMemoryInvokerRepository(), new SimpleInterceptorChainFactory(interceptors), invokerFactories,
        new DefaultUnitOfWorkFactory());
  }
}
