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

package eu.tripledframework.demo.security;

import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.internal.domain.InterceptorChain;
import eu.tripledframework.eventbus.internal.domain.UnitOfWork;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextPropagationInterceptor implements EventBusInterceptor {
  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event, UnitOfWork unitOfWork) {
    unitOfWork.addData("securityContext", SecurityContextHolder.getContext());
    return chain.proceed();
  }
}
