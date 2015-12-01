package eu.tripledframework.eventstore.domain;

import java.util.Collection;

public interface EventRepository {

  Collection<DomainEvent> findAllById(String id);

  Collection<DomainEvent> findAllByIdAndAfterRevision(String identifier, int revision);

  void save(DomainEvent object);
}
