package eu.tripledframework.eventstore.infrastructure;

import eu.tripledframework.eventstore.domain.DomainEvent;
import eu.tripledframework.eventstore.domain.EventRepository;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

public class InMemoryEventRepositoryTest {

  private EventRepository eventRepository;

  @Before
  public void setUp() throws Exception {
    eventRepository = new InMemoryEventRepository();
  }

  @Test
  public void whenOneEventIsStored_shouldBeAbleToFindIt() throws Exception {
    // given
    DomainEvent domainEvent = new DomainEvent("rootIdentifier");

    // when
    eventRepository.save(domainEvent);

    // then
    assertThat(eventRepository.findAllById(domainEvent.getAggregateRootIdentifier()), hasItem(domainEvent));
  }
}