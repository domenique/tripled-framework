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
package eu.tripledframework.eventbus.internal.domain;

import eu.tripledframework.eventbus.EventPublisher;

/**
 * A unit of work is created when executing a command. It can be used to hold contextual information about the running command and
 * will be used to delay the processing of events which are published during the execution of a command.
 */
public interface UnitOfWork {

  void commit(EventPublisher eventPublisher);

  void rollback();

  boolean isRunning();

  void addData(String key, Object value);

  void scheduleEvent(Object event);

  Object getData(String key);
}
