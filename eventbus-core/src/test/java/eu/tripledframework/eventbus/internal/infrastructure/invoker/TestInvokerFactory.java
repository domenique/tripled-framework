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
package eu.tripledframework.eventbus.internal.infrastructure.invoker;

import eu.tripledframework.eventbus.internal.domain.Invoker;
import eu.tripledframework.eventbus.internal.domain.InvokerFactory;

import java.util.Collections;
import java.util.List;

public class TestInvokerFactory implements InvokerFactory {

  public boolean isCreateCalled;

  @Override
  public List<Invoker> create(Object eventHandler) {
    isCreateCalled = true;
    return Collections.emptyList();
  }

  @Override
  public boolean supports(Object object) {
    return true;
  }
}
