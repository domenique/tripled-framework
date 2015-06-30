package eu.tripledframework.eventstore.domain;

import eu.tripledframework.eventstore.infrastructure.ReflectionObjectConstructor;

import java.util.Collection;

public class EventStore<AggregateRootType> {

  private Class<AggregateRootType> type;
  private final EventRepository eventRepository;

  public EventStore(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  public AggregateRootType findById(String aggregateRootIdentifier) {
    Collection<DomainEvent> allEvents = eventRepository.findAllById(aggregateRootIdentifier);

    // construct the aggregate root
    return new ReflectionObjectConstructor<AggregateRootType>(type).construct(allEvents);
  }
}
