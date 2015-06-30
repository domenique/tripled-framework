package eu.tripledframework.eventstore.event;

import eu.tripledframework.eventstore.domain.DomainEvent;

public class MyAggregateRootCreatedEvent extends DomainEvent {
    private String name;

    public MyAggregateRootCreatedEvent(String identifier, String name) {
        super(identifier);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}