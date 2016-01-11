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
package eu.tripledframework.eventbus;


/**
 * Basic interface to interact with the eventbus when publishing events. The events are published in a fire and forget mode. Implying that
 * we do not expect any return types, nor do we even expect that there is any event handler interested in the event.
 * <p>
 * Depending on the configuration of the event bus, it could mean that the event is applied transactionally. This means that the execution
 * of the event handling code is deferred until the completion of a transaction of the command handler firing the event.
 */
public interface EventPublisher {


  /**
   * Publishes the given event.
   *
   * @param event The event to publish.
   */
  void publish(Object event);
}
