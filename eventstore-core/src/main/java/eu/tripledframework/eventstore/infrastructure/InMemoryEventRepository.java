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
