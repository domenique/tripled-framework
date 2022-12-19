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
package eu.tripledframework.eventstore.domain;

import java.util.Collection;
import java.util.Optional;

import eu.tripledframework.eventstore.domain.snapshot.Snapshot;
import eu.tripledframework.eventstore.domain.snapshot.SnapshotRepository;
import eu.tripledframework.eventstore.infrastructure.ReflectionObjectConstructor;

public class EventStore<AggregateRootType> {

  private Class<AggregateRootType> type;
  private final EventRepository eventRepository;
  private SnapshotRepository<AggregateRootType> snapshotRepository;
  private final ReflectionObjectConstructor<AggregateRootType> objectConstructor;

  public EventStore(Class<AggregateRootType> type, EventRepository eventRepository) {
    this.type = type;
    this.eventRepository = eventRepository;
    this.objectConstructor = new ReflectionObjectConstructor<>(type);
  }

  public Optional<AggregateRootType> findById(String identifier) {
    var retrievedSnapshot = retrieveSnapshot(identifier);

    if (retrievedSnapshot.isPresent()) {
      var snapshot = retrievedSnapshot.get();
      var allEvents =
          eventRepository.findAllByIdAndAfterRevision(identifier, snapshot.getRevision());

      return Optional.ofNullable(objectConstructor.applyDomainEvents(snapshot.getAggregateRoot(), allEvents));
    } else {
      var allEvents = eventRepository.findAllById(identifier);

      return Optional.ofNullable(objectConstructor.construct(allEvents));
    }
  }

  private Optional<Snapshot<AggregateRootType>> retrieveSnapshot(String identifier) {
    if (snapshotRepository == null) {
      return Optional.empty();
    }
    return snapshotRepository.findLatest(identifier);
  }

  public void setSnapshotRepository(SnapshotRepository<AggregateRootType> snapshotRepository) {
    this.snapshotRepository = snapshotRepository;
  }
}
