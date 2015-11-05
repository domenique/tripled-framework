package eu.tripledframework.eventstore.domain;

import java.util.Collection;

import eu.tripledframework.eventstore.infrastructure.ReflectionObjectConstructor;

public class EventStore<AggregateRootType> {

  private Class<AggregateRootType> type;
  private final EventRepository eventRepository;

  public EventStore(Class<AggregateRootType> type, EventRepository eventRepository) {
    this.type = type;
    this.eventRepository = eventRepository;
  }

  public AggregateRootType findById(String aggregateRootIdentifier) {
    Collection<DomainEvent> allEvents = eventRepository.findAllById(aggregateRootIdentifier);

    // construct the aggregate root
    return new ReflectionObjectConstructor<>(type).construct(allEvents);
  }
}
