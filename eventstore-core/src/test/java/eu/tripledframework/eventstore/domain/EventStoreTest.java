package eu.tripledframework.eventstore.domain;

import org.junit.Before;
import org.junit.Test;

import eu.tripledframework.eventstore.event.MyAggregateRootCreatedEvent;
import eu.tripledframework.eventstore.infrastructure.InMemoryEventRepository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class EventStoreTest {

  private EventStore<MyAggregateRoot> eventStore;
  private InMemoryEventRepository inMemoryEventRepository;

  @Before
  public void setUp() throws Exception {
    inMemoryEventRepository = new InMemoryEventRepository();
    eventStore = new EventStore<>(MyAggregateRoot.class, inMemoryEventRepository);
  }

  @Test
  public void whenCallingFind_shouldReturnAggregateIfEventsAreFound() throws Exception {
    // given
    MyAggregateRootCreatedEvent createdEvent = new MyAggregateRootCreatedEvent("id", "name");
    inMemoryEventRepository.save(createdEvent);

    // when
    MyAggregateRoot aggregate = eventStore.findById(createdEvent.getAggregateRootIdentifier());

    // then
    assertThat(aggregate.name, equalTo(createdEvent.getName()));
  }
}