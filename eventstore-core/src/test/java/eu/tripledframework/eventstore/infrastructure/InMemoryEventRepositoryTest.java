/*
 * Copyright 2015 TripleD, DTI-Consulting.
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
import eu.tripledframework.eventstore.domain.EventRepository;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

public class InMemoryEventRepositoryTest {

  private EventRepository eventRepository;

  @Before
  public void setUp() throws Exception {
    eventRepository = new InMemoryEventRepository();
  }

  @Test
  public void whenOneEventIsStored_shouldBeAbleToFindIt() throws Exception {
    // given
    DomainEvent domainEvent = new DomainEvent("rootIdentifier");

    // when
    eventRepository.save(domainEvent);

    // then
    assertThat(eventRepository.findAllById(domainEvent.getAggregateRootIdentifier()), hasItem(domainEvent));
  }
}