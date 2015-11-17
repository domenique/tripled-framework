package eu.tripledframework.eventstore.domain;

import java.lang.reflect.InvocationTargetException;

import eu.tripledframework.eventstore.domain.annotation.ConstructionHandler;
import eu.tripledframework.eventstore.domain.annotation.EP;
import eu.tripledframework.eventstore.event.AddressUpdatedEvent;
import eu.tripledframework.eventstore.event.AddressUpdatedEventWhichCannotBeInvoked;
import eu.tripledframework.eventstore.event.MyAggregateRootCreatedEvent;

public class MyAggregateRoot implements ConstructionAware {

  public String identifier;
  public String name;
  public String address;
  public boolean postReconstructCalled;
  public boolean constructorCalled;
  public boolean updateAddressCalled;
  public boolean updateAddressWhichCannotBeInvokedCalled;

  @ConstructionHandler(MyAggregateRootCreatedEvent.class)
  public MyAggregateRoot(@EP("aggregateRootIdentifier") String identifier, @EP("name") String name) {
    this.identifier = identifier;
    this.name = name;
    this.constructorCalled = true;
  }

  @ConstructionHandler(AddressUpdatedEvent.class)
  public void updateAddress(@EP("address") String address) {
    this.address = address;
    this.updateAddressCalled = true;
  }

  @ConstructionHandler(AddressUpdatedEventWhichCannotBeInvoked.class)
  public void updateAddressWhichCannotBeInvoked() throws InvocationTargetException {
    this.updateAddressWhichCannotBeInvokedCalled = true;
    throw new InvocationTargetException(null, "oeps");
  }

  @Override
  public void postConstruct() {
    this.postReconstructCalled = true;
  }

  public void reset() {
    constructorCalled = false;
    updateAddressCalled = false;
    updateAddressWhichCannotBeInvokedCalled = false;
  }
}