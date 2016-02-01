/*
 * Copyright 2016 TripleD framework.
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
package eu.tripledframework.eventbus.internal.infrastructure.callback;

import eu.tripledframework.eventbus.CommandCallback;
import eu.tripledframework.eventbus.internal.domain.CallbackTemplate;

import java.util.function.Supplier;

public class SimpleCallbackTemplate<ReturnType> implements CallbackTemplate<ReturnType> {

  private CommandCallback<ReturnType> eventCallback;

  public SimpleCallbackTemplate(CommandCallback<ReturnType> eventCallback) {
    this.eventCallback = eventCallback;
  }

  @Override
  public void doWithCallback(Supplier<ReturnType> supplier) {
    ReturnType response = null;
    RuntimeException thrownException = null;
    try {
      response = supplier.get();
    } catch (RuntimeException exception) {
      thrownException = exception;
    }
    invokeAppropriateCallbackMethod(response, thrownException);
  }

  private void invokeAppropriateCallbackMethod(ReturnType response, RuntimeException thrownException) {
    if (thrownException != null) {
      eventCallback.onFailure(thrownException);
    } else {
      eventCallback.onSuccess(response);
    }
  }
}
