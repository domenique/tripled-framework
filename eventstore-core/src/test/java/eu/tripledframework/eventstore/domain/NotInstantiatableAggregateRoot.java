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
package eu.tripledframework.eventstore.domain;

import eu.tripledframework.eventstore.domain.annotation.ConstructionHandler;
import eu.tripledframework.eventstore.domain.annotation.EP;
import eu.tripledframework.eventstore.event.MyAggregateRootCreatedEvent;

public abstract class NotInstantiatableAggregateRoot implements ConstructionAware {

  private final String identifier;
  private final String name;

  @ConstructionHandler(MyAggregateRootCreatedEvent.class)
  public NotInstantiatableAggregateRoot(@EP("aggregateRootIdentifier") String identifier, @EP("name") String name) {
    this.identifier = identifier;
    this.name = name;
  }

}
