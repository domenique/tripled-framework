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

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.InterceptorChain;

public class TestEventBusInterceptor implements EventBusInterceptor {

  public boolean isInterceptorCalled = false;

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event) {
    isInterceptorCalled = true;
    return chain.proceed();
  }
}
