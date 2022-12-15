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
package eu.tripledframework.eventstore.infrastructure;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import eu.tripledframework.eventstore.domain.DomainEvent;
import eu.tripledframework.eventstore.domain.EventRepository;

public class InMemoryEventRepository implements EventRepository {

  private final List<DomainEvent> events;

  public InMemoryEventRepository() {
    events = new CopyOnWriteArrayList<>();
  }

  @Override
  public Collection<DomainEvent> findAllById(String id) {
    return events.stream()
        .filter(domainEvent -> domainEvent.getAggregateRootIdentifier().equals(id))
        .collect(Collectors.toList());
  }

  @Override
  public Collection<DomainEvent> findAllByIdAndAfterRevision(String identifier, int revision) {
    return events.stream()
        .filter(domainEvent -> domainEvent.getAggregateRootIdentifier().equals(identifier))
        .filter(domainEvent -> domainEvent.getRevision() > revision)
        .collect(Collectors.toList());
  }

  @Override
  public void save(DomainEvent object) {
    events.add(object);
  }
}
