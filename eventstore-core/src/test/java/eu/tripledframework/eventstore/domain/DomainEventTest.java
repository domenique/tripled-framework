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
package eu.tripledframework.eventstore.domain;


import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DomainEventTest {

    @Test
    public void whenCreating_shouldInstantiateTimestampAndId() throws Exception {
        // given
        String sourceIdentifier = "sourceIdentifier";

        // when
        DomainEvent event = new DomainEvent(sourceIdentifier);

        // then
        assertThat(event.getTimestamp(), notNullValue());
        assertThat(event.getId(), notNullValue());
        assertThat(event.getAggregateRootIdentifier(), equalTo(sourceIdentifier));
    }

    @Test
    public void whenCreatingWithProtectedConstructor_shouldNotInstantiateTimestampAndId() throws Exception {
        // given

        // when
        DomainEvent event = new DomainEvent();

        // then
        assertThat(event.getTimestamp(), nullValue());
        assertThat(event.getId(), nullValue());
        assertThat(event.getAggregateRootIdentifier(), nullValue());
    }

    @Test
    public void whenCreating_ShouldNotBeEqual() throws Exception {
        // given
        String sourceIdentifier = "sourceIdentifier";
        DomainEvent event1 = new DomainEvent(sourceIdentifier);
        DomainEvent event2 = new DomainEvent(sourceIdentifier);

        // when
        boolean response = event1.equals(event2);

        // then
        assertThat(response, is(false));
    }

    @Test
    public void whenComparingWithNull_ShouldNotBeEqual() throws Exception {
        // given
        String sourceIdentifier = "sourceIdentifier";
        DomainEvent event1 = new DomainEvent(sourceIdentifier);

        // when
        boolean response = event1.equals(null);

        // then
        assertThat(response, is(false));
    }

    @Test
    public void whenComparingSameInstance_ShouldBeEqual() throws Exception {
        // given
        String sourceIdentifier = "sourceIdentifier";
        DomainEvent event1 = new DomainEvent(sourceIdentifier);

        // when
        boolean response = event1.equals(event1);

        // then
        assertThat(response, is(true));
    }

    @Test
    public void whenCreatingHashOfTwoObject_ShouldNotBeEqual() throws Exception {
        // given
        String sourceIdentifier = "sourceIdentifier";
        DomainEvent event1 = new DomainEvent(sourceIdentifier);
        DomainEvent event2 = new DomainEvent(sourceIdentifier);

        // when
        int hashForEvent1 = event1.hashCode();
        int hashForEvent2 = event2.hashCode();

        // then
        assertThat(hashForEvent1, not(equalTo(hashForEvent2)));
    }
}