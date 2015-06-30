package eu.tripledframework.eventstore.domain;

import eu.tripledframework.eventstore.domain.annotation.ConstructionHandler;
import eu.tripledframework.eventstore.domain.annotation.EP;
import eu.tripledframework.eventstore.event.AddressUpdatedEvent;
import eu.tripledframework.eventstore.event.AddressUpdatedEventWhichCannotBeInvoked;
import eu.tripledframework.eventstore.event.MyAggregateRootCreatedEvent;

import java.lang.reflect.InvocationTargetException;

public class MyAggregateRoot implements ConstructionAware {

    protected String identifier;
    protected String name;
    protected String address;
    protected boolean postReconstructCalled;

    @ConstructionHandler(MyAggregateRootCreatedEvent.class)
    public MyAggregateRoot(@EP("aggregateRootIdentifier") String identifier, @EP("name") String name) {
        this.identifier = identifier;
        this.name = name;
    }

    @ConstructionHandler(AddressUpdatedEvent.class)
    public void updateAddress(@EP("address") String address) {
        this.address = address;
    }

    @ConstructionHandler(AddressUpdatedEventWhichCannotBeInvoked.class)
    public void updateAddressWhichCannotBeInvoked() throws InvocationTargetException {
        throw new InvocationTargetException(null, "oeps");
    }

    @Override
    public void postConstruct() {
        this.postReconstructCalled = true;
    }
}