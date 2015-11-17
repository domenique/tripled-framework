package eu.tripledframework.eventstore.domain;

import java.util.Collection;

public interface ObjectConstructor<T> {

  T construct(Collection<DomainEvent> events);

  T applyDomainEvents(T instance, Collection<DomainEvent> events);
}
