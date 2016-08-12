/*
 * Copyright 2015 TripleD framework.
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
package eu.tripledframework.demo.infrastructure;

import java.util.ArrayList;
import java.util.List;

import eu.tripledframework.eventbus.Handler;
import eu.tripledframework.eventbus.Handles;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.tripledframework.demo.model.SaidHelloDomainEvent;

@Handler
@Component
public class InMemoryHelloEventStore {

  private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryHelloEventStore.class);

  private List<Object> events;

  public InMemoryHelloEventStore() {
    this.events = new ArrayList<>();
  }

  @Handles(SaidHelloDomainEvent.class)
  public void handleSaidHelloDomainEvent(SaidHelloDomainEvent event) {
    LOGGER.info("Received SaidHelloDomainEvent.");
    this.events.add(event);
  }
}
