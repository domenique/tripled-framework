package eu.tripledframework.eventstore.infrastructure;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

import eu.tripledframework.eventstore.domain.DomainEvent;
import eu.tripledframework.eventstore.domain.MyAggregateRoot;
import eu.tripledframework.eventstore.domain.NotInstantiatableAggregateRoot;
import eu.tripledframework.eventstore.domain.ObjectConstructor;
import eu.tripledframework.eventstore.event.AddressUpdatedEvent;
import eu.tripledframework.eventstore.event.AddressUpdatedEventWhichCannotBeInvoked;
import eu.tripledframework.eventstore.event.MyAggregateRootCreatedEvent;
import eu.tripledframework.eventstore.event.UnMappedEvent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class ReflectionObjectConstructorTest {

  @Test
  public void whenGivenAnEmptyListOfEvents_ShouldReturnNull() throws Exception {
    // given
    ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

    // when
    MyAggregateRoot object = objectConstructor.construct(Collections.emptyList());

    // then
    assertThat(object, nullValue());
  }

  @Test
  public void whenGivenANullEventList_ShouldReturnNull() throws Exception {
    // given
    ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

    // when
    MyAggregateRoot object = objectConstructor.construct(null);

    // then
    assertThat(object, nullValue());
  }

  @Test
  public void whenGivenAnEmptyListOfEventsAndAnInstance_ShouldReturnInstance() throws Exception {
    // given
    ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

    // when
    MyAggregateRoot instance = new MyAggregateRoot("id", "name");
    MyAggregateRoot object = objectConstructor.applyDomainEvents(instance, Collections.emptyList());

    // then
    assertThat(object, sameInstance(instance));
  }

  @Test
  public void whenGivenAnNullListOfEventsAndAnInstance_ShouldReturnInstance() throws Exception {
    // given
    ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

    // when
    MyAggregateRoot instance = new MyAggregateRoot("id", "name");
    MyAggregateRoot object = objectConstructor.applyDomainEvents(instance, null);

    // then
    assertThat(object, sameInstance(instance));
  }

  @Test
  public void whenGivenOneEvent_ShouldCreateInstance() throws Exception {
    // given
    String sourceIdentifier = UUID.randomUUID().toString();
    MyAggregateRootCreatedEvent event = new MyAggregateRootCreatedEvent(sourceIdentifier, "Wallstreet");

    ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

    // when
    MyAggregateRoot instance = objectConstructor.construct(Arrays.asList(event));

    // then
    assertThat(instance.identifier, equalTo(sourceIdentifier));
    assertThat(instance.name, equalTo(event.getName()));
    assertThat(instance.address, nullValue());
    assertThat(instance.postReconstructCalled, is(true));
    assertThat(event.getId(), notNullValue());
    assertThat(event.getAggregateRootIdentifier(), notNullValue());
    assertThat(event.getTimestamp(), notNullValue());
  }

  @Test
  public void whenGivenTwoEvents_ShouldCreateInstanceWithEventsReplayed() throws Exception {
    // given
    String sourceIdentifier = UUID.randomUUID().toString();
    MyAggregateRootCreatedEvent event = new MyAggregateRootCreatedEvent(sourceIdentifier, "Wallstreet");
    AddressUpdatedEvent secondEvent = new AddressUpdatedEvent(sourceIdentifier, "streetName streetNumber");

    ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

    // when
    MyAggregateRoot instance = objectConstructor.construct(Arrays.asList(event, secondEvent));

    // then
    assertThat(instance.identifier, equalTo(sourceIdentifier));
    assertThat(instance.name, equalTo(event.getName()));
    assertThat(instance.address, equalTo(secondEvent.getAddress()));
    assertThat(instance.postReconstructCalled, is(true));
  }

  @Test(expected = AggregateRootReconstructionException.class)
  public void whenGivenTwoEventsOutOfOrder_ShouldThrowException() throws Exception {
    // given
    String identifier = UUID.randomUUID().toString();
    DomainEvent event = new MyAggregateRootCreatedEvent(identifier, "Wallstreet");
    DomainEvent secondEvent = new AddressUpdatedEvent(identifier, "streetName streetNumber");

    ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

    // when
    MyAggregateRoot instance = objectConstructor.construct(Arrays.asList(secondEvent, event));

    // then
    // exception
  }

  @Test(expected = AggregateRootReconstructionException.class)
  public void whenGivenEventThatHasNoConstructor_ShouldThrowException() throws Exception {
    // given
    DomainEvent unMappedEvent = new UnMappedEvent();

    ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

    // when
    MyAggregateRoot instance = objectConstructor.construct(Arrays.asList(unMappedEvent));

    // then
    // exception
  }

  @Test(expected = AggregateRootReconstructionException.class)
  public void whenGivenEventThatHasNoMethod_ShouldThrowException() throws Exception {
    // given
    DomainEvent event = new MyAggregateRootCreatedEvent(UUID.randomUUID().toString(), "Wallstreet");
    DomainEvent unMappedEvent = new UnMappedEvent();

    ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

    // when
    MyAggregateRoot instance = objectConstructor.construct(Arrays.asList(event, unMappedEvent));

    // then
    // exception
  }

  @Test(expected = AggregateRootReconstructionException.class)
  public void whenGivenAnEventForAnObjectWhichCannotBeInstantiated_shouldThrowAnException() throws Exception {
    // given
    DomainEvent event = new MyAggregateRootCreatedEvent(null, null);
    ObjectConstructor<NotInstantiatableAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(NotInstantiatableAggregateRoot.class);

    // when
    objectConstructor.construct(Arrays.asList(event));

    // then -> exception

  }

  @Test(expected = AggregateRootReconstructionException.class)
  public void whenGivenAnEventWhichCannotBeInvoked_shouldThrowAnException() throws Exception {
    // given
    DomainEvent event = new MyAggregateRootCreatedEvent(UUID.randomUUID().toString(), "Wallstreet");
    DomainEvent secondEvent = new AddressUpdatedEventWhichCannotBeInvoked();
    ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

    // when
    objectConstructor.construct(Arrays.asList(event, secondEvent));

    // then -> exception

  }
}