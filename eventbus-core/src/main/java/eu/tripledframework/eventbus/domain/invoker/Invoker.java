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
package eu.tripledframework.eventbus.domain.invoker;

/**
 * An Invoker is responsible for invoking a method which handles an event or command.
 * <p>
 * This method can be on a simple object, or if the implementation supports it, it could be made in such a way that
 * it instantiates the object before calling the method.
 */
public interface Invoker {

  /**
   * Method to verify if this Invoker is able to invoke an event of the given type.
   *
   * @param eventTypeToHandle The type of event that needs to be checked.
   * @return <code>true</code> if the Invoker is able to handle events of the given type,
   * <code>false</code> otherwise.
   */
  boolean handles(Class<?> eventTypeToHandle);

  /**
   * Method which checks if the method which is being invoked by this invoker has a return type.
   *
   * @return <code>true</code> if the method which will be invoked has a return type, <code>false</code> otherwise.
   */
  boolean hasReturnType();

  /**
   * Invokes the method handler with the given object as input.
   *
   * @param object The event with which the method handler should be invoked.
   * @return an optional response from the method handler.
   */
  Object invoke(Object object);
}
