package eu.tripledframework.eventstore.domain;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import eu.tripledframework.eventstore.domain.snapshot.Snapshot;
import eu.tripledframework.eventstore.event.AddressUpdatedEvent;
import eu.tripledframework.eventstore.event.MyAggregateRootCreatedEvent;
import eu.tripledframework.eventstore.infrastructure.InMemoryEventRepository;
import eu.tripledframework.eventstore.infrastructure.snapshot.InMemorySnapshotRepository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EventStoreTest {

  private EventStore<MyAggregateRoot> eventStore;
  private InMemoryEventRepository inMemoryEventRepository;
  private InMemorySnapshotRepository<MyAggregateRoot> inMemorySnapshotRepository;

  @Before
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