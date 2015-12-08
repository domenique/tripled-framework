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

package eu.tripledframework.eventbus.domain;

import java.util.concurrent.Future;

/**
 * The basic contract of the Bus when dispatching commands.
 * <p>
 * An EventDispatcher is responsible for dispatching commands. The Bus can, depending on the implementation
 * execute the command handling in a synchronous or asynchronous fashion.
 */
public interface CommandDispatcher {

  /**
   * Dispatches the given Command and invokes the given callback with either success or failure.
   *
   * @param command The command to dispatch.
   * @param callback The callback to invoke upon completion.
   * @param <ReturnType>   The return type of the command handling.
   */
  <ReturnType> void dispatch(Object command, EventCallback<ReturnType> callback);

  /**
   * Dispatches the given command and invokes the given callback with either success or failure.
   *
   * @param command The command to dispatch.
   * @param <ReturnType>   The return type of the command handling.
   * @return A future object to be used to retrieve the result.
   */
  <ReturnType> Future<ReturnType> dispatch(Object command);

}
