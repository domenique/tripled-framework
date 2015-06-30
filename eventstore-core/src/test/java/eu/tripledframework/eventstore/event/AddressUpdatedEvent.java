package eu.tripledframework.eventstore.event;

import eu.tripledframework.eventstore.domain.DomainEvent;

public class AddressUpdatedEvent extends DomainEvent {
    private String address;

    public AddressUpdatedEvent(String identifier, String address) {
        super(identifier);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
