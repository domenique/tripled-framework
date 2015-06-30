package eu.tripledframework.eventstore.domain;

import java.util.Collection;

public interface EventRepository {

  Collection<DomainEvent> findAllById(String id);

  void save(DomainEvent object);
}
