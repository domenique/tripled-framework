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
import eu.tripledframework.eventbus.internal.domain.UnitOfWorkTemplate;

import java.util.function.Supplier;

public class SimpleUnitOfWorkTemplate implements UnitOfWorkTemplate {

  private EventPublisher eventPublisher;
  private UnitOfWork unitOfWork;

  public SimpleUnitOfWorkTemplate(EventPublisher eventPublisher, UnitOfWork unitOfWork) {
    this.eventPublisher = eventPublisher;
    this.unitOfWork = unitOfWork;
  }

  @Override
  public <ReturnType> ReturnType doWithUnitOfWork(Supplier<ReturnType> supplier) {
    UnitOfWorkManager.store(unitOfWork);

    try {
      ReturnType response = supplier.get();
      UnitOfWorkManager.get().commit(eventPublisher);
      return response;
    } catch (RuntimeException exception) {
      UnitOfWorkManager.get().rollback();
      throw exception;
    } finally {
      UnitOfWorkManager.clear();
    }

  }
}
