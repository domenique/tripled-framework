package eu.tripledframework.eventstore.domain;

import java.util.Collection;

public interface EventRepository {

  Collection<Event> findAllById(String id);

  void save(Event object);
}
