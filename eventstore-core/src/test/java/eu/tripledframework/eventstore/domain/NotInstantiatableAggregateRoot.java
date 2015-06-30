package eu.tripledframework.eventstore.domain;

import eu.tripledframework.eventstore.domain.annotation.ConstructionHandler;
import eu.tripledframework.eventstore.domain.annotation.EP;
import eu.tripledframework.eventstore.event.MyAggregateRootCreatedEvent;

public abstract class NotInstantiatableAggregateRoot implements ConstructionAware {

  private final String identifier;
  private final String name;

  @ConstructionHandler(MyAggregateRootCreatedEvent.class)
  public NotInstantiatableAggregateRoot(@EP("aggregateRootIdentifier") String identifier, @EP("name") String name) {
    this.identifier = identifier;
    this.name = name;
  }

}
