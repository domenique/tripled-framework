/*
 * Copyright 2022 TripleD framework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.tripledframework.eventstore.infrastructure;

import eu.tripledframework.eventstore.domain.DomainEvent;
import eu.tripledframework.eventstore.domain.MyAggregateRoot;
import eu.tripledframework.eventstore.domain.NotInstantiatableAggregateRoot;
import eu.tripledframework.eventstore.domain.ObjectConstructor;
import eu.tripledframework.eventstore.event.AddressUpdatedEvent;
import eu.tripledframework.eventstore.event.AddressUpdatedEventWhichCannotBeInvoked;
import eu.tripledframework.eventstore.event.MyAggregateRootCreatedEvent;
import eu.tripledframework.eventstore.event.UnMappedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReflectionObjectConstructorTest {

    @Test
    void whenGivenAnEmptyListOfEvents_ShouldReturnNull() {
        // given
        ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

        // when
      var object = objectConstructor.construct(Collections.emptyList());

        // then
        assertThat(object, nullValue());
    }

    @Test
    void whenGivenANullEventList_ShouldReturnNull() {
        // given
        ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

        // when
      var object = objectConstructor.construct(null);

        // then
        assertThat(object, nullValue());
    }

    @Test
    void whenGivenAnEmptyListOfEventsAndAnInstance_ShouldReturnInstance() {
        // given
        ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

        // when
      var instance = new MyAggregateRoot("id", "name");
      var object = objectConstructor.applyDomainEvents(instance, Collections.emptyList());

        // then
        assertThat(object, sameInstance(instance));
    }

    @Test
    void whenGivenAnNullListOfEventsAndAnInstance_ShouldReturnInstance() {
        // given
        ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

        // when
      var instance = new MyAggregateRoot("id", "name");
      var object = objectConstructor.applyDomainEvents(instance, null);

        // then
        assertThat(object, sameInstance(instance));
    }

    @Test
    void whenGivenOneEvent_ShouldCreateInstance() {
        // given
      var sourceIdentifier = UUID.randomUUID().toString();
      var event = new MyAggregateRootCreatedEvent(sourceIdentifier, "Wallstreet");

        ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

        // when
      var instance = objectConstructor.construct(Arrays.asList(event));

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
    void whenGivenTwoEvents_ShouldCreateInstanceWithEventsReplayed() {
        // given
      var sourceIdentifier = UUID.randomUUID().toString();
      var event = new MyAggregateRootCreatedEvent(sourceIdentifier, "Wallstreet");
      var secondEvent = new AddressUpdatedEvent(sourceIdentifier, "streetName streetNumber");

        ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

        // when
      var instance = objectConstructor.construct(Arrays.asList(event, secondEvent));

        // then
        assertThat(instance.identifier, equalTo(sourceIdentifier));
        assertThat(instance.name, equalTo(event.getName()));
        assertThat(instance.address, equalTo(secondEvent.getAddress()));
        assertThat(instance.postReconstructCalled, is(true));
    }

    @Test
    void whenGivenTwoEventsOutOfOrder_ShouldThrowException() {
        // given
      var identifier = UUID.randomUUID().toString();
        DomainEvent event = new MyAggregateRootCreatedEvent(identifier, "Wallstreet");
        DomainEvent secondEvent = new AddressUpdatedEvent(identifier, "streetName streetNumber");

        ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

        // when
        Assertions.assertThrows(AggregateRootReconstructionException.class, () -> {
          var instance = objectConstructor.construct(Arrays.asList(secondEvent, event));
        });

        // then
        // exception
    }

    @Test
    void whenGivenEventThatHasNoConstructor_ShouldThrowException() {
        // given
        DomainEvent unMappedEvent = new UnMappedEvent();

        ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

        // when
        Assertions.assertThrows(AggregateRootReconstructionException.class, () -> {
          var instance = objectConstructor.construct(Arrays.asList(unMappedEvent));
        });

        // then
        // exception
    }

    @Test
    void whenGivenEventThatHasNoMethod_ShouldThrowException() {
        // given
        DomainEvent event = new MyAggregateRootCreatedEvent(UUID.randomUUID().toString(), "Wallstreet");
        DomainEvent unMappedEvent = new UnMappedEvent();

        ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

        // when
        Assertions.assertThrows(AggregateRootReconstructionException.class, () -> {
          var instance = objectConstructor.construct(Arrays.asList(event, unMappedEvent));
        });

        // then
        // exception
    }

    @Test
    void whenGivenAnEventForAnObjectWhichCannotBeInstantiated_shouldThrowAnException() {
        // given
        DomainEvent event = new MyAggregateRootCreatedEvent(null, null);
        ObjectConstructor<NotInstantiatableAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(NotInstantiatableAggregateRoot.class);

        // when
        Assertions.assertThrows(AggregateRootReconstructionException.class, () -> {
            objectConstructor.construct(Arrays.asList(event));
        });

        // then -> exception

    }

    @Test
    void whenGivenAnEventWhichCannotBeInvoked_shouldThrowAnException() {
        // given
        DomainEvent event = new MyAggregateRootCreatedEvent(UUID.randomUUID().toString(), "Wallstreet");
        DomainEvent secondEvent = new AddressUpdatedEventWhichCannotBeInvoked();
        ObjectConstructor<MyAggregateRoot> objectConstructor = new ReflectionObjectConstructor<>(MyAggregateRoot.class);

        // when
        Assertions.assertThrows(AggregateRootReconstructionException.class, () -> {
            objectConstructor.construct(Arrays.asList(event, secondEvent));
        });

        // then -> exception

    }
}