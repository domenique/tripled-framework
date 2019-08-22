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

import java.util.Optional;

import eu.tripledframework.eventstore.domain.snapshot.Snapshot;
import eu.tripledframework.eventstore.event.AddressUpdatedEvent;
import eu.tripledframework.eventstore.event.MyAggregateRootCreatedEvent;
import eu.tripledframework.eventstore.infrastructure.InMemoryEventRepository;
import eu.tripledframework.eventstore.infrastructure.snapshot.InMemorySnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EventStoreTest {

  private EventStore<MyAggregateRoot> eventStore;
  private InMemoryEventRepository inMemoryEventRepository;
  private InMemorySnapshotRepository<MyAggregateRoot> inMemorySnapshotRepository;

  @BeforeEach
  public void setUp() throws Exception {
    inMemoryEventRepository = new InMemoryEventRepository();
    inMemorySnapshotRepository = new InMemorySnapshotRepository<>();
    eventStore = new EventStore<>(MyAggregateRoot.class, inMemoryEventRepository);

  }

  @Test
  public void whenNotFindingAnyEvents_shouldReturnNull() throws Exception {
    // given

    // when
    Optional<MyAggregateRoot> aggregateRoot = eventStore.findById("nonExisting");

    // then
    assertThat(aggregateRoot.orElse(null), nullValue());
  }

  @Test
  public void whenCallingFind_shouldReturnAggregateIfEventsAreFound() throws Exception {
    // given
    MyAggregateRootCreatedEvent createdEvent = new MyAggregateRootCreatedEvent("id", "name");
    inMemoryEventRepository.save(createdEvent);

    // when
    Optional<MyAggregateRoot> aggregate = eventStore.findById(createdEvent.getAggregateRootIdentifier());

    // then
    assertThat(aggregate.get().name, equalTo(createdEvent.getName()));
  }

  @Test
  public void whenFindingASnapshot_shouldUseItToConstructAggregate() throws Exception {
    // given
    eventStore.setSnapshotRepository(inMemorySnapshotRepository);

    String aggregateRootId = "id";
    MyAggregateRootCreatedEvent createdEvent = new MyAggregateRootCreatedEvent(aggregateRootId, "name", 0);
    inMemoryEventRepository.save(createdEvent);

    MyAggregateRoot myAggregateRoot = new MyAggregateRoot(aggregateRootId, "name");
    Snapshot<MyAggregateRoot> snapshot = new Snapshot<>(myAggregateRoot, aggregateRootId, 0);
    inMemorySnapshotRepository.save(snapshot);

    AddressUpdatedEvent updatedEvent = new AddressUpdatedEvent(aggregateRootId, 1, "theAddress");
    inMemoryEventRepository.save(updatedEvent);

    // reset flags to be able to assert on them later.
    myAggregateRoot.reset();

    // when
    Optional<MyAggregateRoot> aggregate = eventStore.findById(aggregateRootId);

    // then
    assertThat(aggregate.isPresent(), is(true));
    assertThat(aggregate.get(), sameInstance(myAggregateRoot));
    assertThat(aggregate.get().address, equalTo(updatedEvent.getAddress()));
    assertThat(aggregate.get().postReconstructCalled, equalTo(true));
    assertThat(aggregate.get().constructorCalled, equalTo(false));
    assertThat(aggregate.get().updateAddressCalled, equalTo(true));
  }
}