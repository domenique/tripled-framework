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

package eu.tripledframework.eventbus.domain.invoker;

import java.util.List;

/**
 * A Factory to create EventHandlerInvoker objects.
 * <p>
 * Override this class if you want the eventbus to create alternative EventHandlerInvoker objects.
 * <p>
 * When subscribing an object to the eventbus, the appropriate factory will be used depending on the result of the
 * supports methods. The Factories will be queried in the order specified in which they are given to the eventBus
 * when it is configured.
 */
public interface EventHandlerInvokerFactory {

  /**
   * Method which will create one or more EventHandlerInvoker objects for the given object.
   *
   * @param eventHandler The object for which EventHandlerInvoker objects should be created.
   * @return A List of EventHandlerInvoker objects, or an empty list. never null.
   */
  List<EventHandlerInvoker> create(Object eventHandler);

  /**
   * Returns true if this factory is capable of creating EventHandlerInvoker objects for the given object,
   * false otherwise.
   *
   * This method is called by the EventBus prior to calling the create.
   * @param object The object for which the test should be done.
   * @return <code>true</code> if this factory supports the given class, <code>false</code> otherwise.
   */
  boolean supports(Object object);
}
