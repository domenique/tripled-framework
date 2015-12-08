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

package eu.tripledframework.eventbus.domain.unitofwork;

import eu.tripledframework.eventbus.domain.CommandDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SimpleUnitOfWork implements UnitOfWork {

  private String id;

  private Map<String, Object> data;
  private List<Object> delayedEvents;
  private CommandDispatcher commandDispatcher;

  public SimpleUnitOfWork(CommandDispatcher commandDispatcher) {
    this.id = UUID.randomUUID().toString();
    this.commandDispatcher = commandDispatcher;

    this.delayedEvents = new ArrayList<>();
    this.data = new HashMap<>();
  }

  public void delayEvent(Object event) {
    this.delayedEvents.add(event);
  }

  public void addProperty(String key, Object object) {
    this.data.put(key, object);
  }

  @Override
  public void commit() {
    delayedEvents.forEach(commandDispatcher::dispatch);
  }

  @Override
  public void rollback() {
    this.delayedEvents = new ArrayList<>();
  }
}
