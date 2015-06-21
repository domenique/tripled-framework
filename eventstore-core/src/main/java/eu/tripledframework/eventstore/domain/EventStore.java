package eu.tripledframework.eventstore.domain;

import java.util.Collection;

public class EventStore<AggregateRootType> {

  private final EventRepository eventRepository;

  public EventStore(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  public AggregateRootType findById(String id) {
    Collection<Event> allEvents = eventRepository.findAllById(id);

    // reconstruct the aggregate root

    return null;
  }
}
