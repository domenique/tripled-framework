/*
 * Copyright 2016 TripleD framework.
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
package eu.tripledframework.eventbus.internal.infrastructure.unitofwork;

import eu.tripledframework.eventbus.EventPublisher;
import eu.tripledframework.eventbus.internal.domain.UnitOfWork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultUnitOfWork implements UnitOfWork {

  private Map<String, Object> contextualData;
  private List<Object> delayedEvents;
  private UnitOfWorkStatus status;


  public DefaultUnitOfWork() {
    contextualData = new HashMap<>();
    delayedEvents = new ArrayList<>();
    status = UnitOfWorkStatus.STARTED;
  }

  @Override
  public void commit(EventPublisher eventPublisher) {
    status = UnitOfWorkStatus.COMMITTING;
    delayedEvents.forEach(eventPublisher::publish);
    status = UnitOfWorkStatus.COMMITED;
  }

  @Override
  public void rollback() {
    status = UnitOfWorkStatus.ROLLING_BACK;
    delayedEvents.clear();
    status = UnitOfWorkStatus.ROLLED_BACK;
  }

  @Override
  public boolean isRunning() {
    return UnitOfWorkStatus.STARTED == status;
  }

  @Override
  public void addData(String key, Object value) {
    contextualData.put(key, value);
  }

  @Override
  public void scheduleEvent(Object event) {
    delayedEvents.add(event);
  }
}
