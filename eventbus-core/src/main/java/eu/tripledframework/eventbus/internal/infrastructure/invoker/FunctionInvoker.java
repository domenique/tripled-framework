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

import java.util.function.Function;

public class FunctionInvoker implements Invoker {

  private final Function<Object, Object> function;

  public FunctionInvoker(Function<Object, Object> function) {
    this.function = function;
  }

  @Override
  public boolean handles(Class<?> eventTypeToHandle) {
    return true;
  }

  @Override
  public boolean hasReturnType() {
    return false;
  }

  @Override
  public Object invoke(Object object) {
    return function.apply(object);
  }


  @Override
  public String toString() {
    return function.getClass().getSimpleName();
  }
}
