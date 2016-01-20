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

import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.internal.domain.InterceptorChain;
import eu.tripledframework.eventbus.internal.domain.InterceptorChainFactory;
import eu.tripledframework.eventbus.internal.domain.Invoker;

import java.util.Collections;
import java.util.List;

public class SimpleInterceptorChainFactory implements InterceptorChainFactory {

  private final List<EventBusInterceptor> interceptors;

  public SimpleInterceptorChainFactory() {
    this.interceptors = Collections.unmodifiableList(Collections.emptyList());
  }

  public SimpleInterceptorChainFactory(List<EventBusInterceptor> interceptors) {
    this.interceptors = Collections.unmodifiableList(interceptors);
  }

  @Override
  public <ReturnType> InterceptorChain<ReturnType> createChain(Object event, List<Invoker> invokers) {
    return new SimpleInterceptorChain<>(event, invokers.iterator(), interceptors.iterator());
  }

  @Override
  public <ReturnType> InterceptorChain<ReturnType> createChain(Object event, Invoker invoker) {
    return new SimpleInterceptorChain<>(event, Collections.singletonList(invoker).iterator(), interceptors.iterator());
  }

}
