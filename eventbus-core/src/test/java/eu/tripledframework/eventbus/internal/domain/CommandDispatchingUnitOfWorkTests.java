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

import eu.tripledframework.eventbus.CommandDispatcher;
import eu.tripledframework.eventbus.EventPublisher;
import eu.tripledframework.eventbus.command.HelloCommand;
import eu.tripledframework.eventbus.event.TestEvent;
import eu.tripledframework.eventbus.handler.TestCommandHandler;
import eu.tripledframework.eventbus.handler.TestEventHandler;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.TestUnitOfWork;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.TestingUnitOfWorkFactory;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.UnitOfWorkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class CommandDispatchingUnitOfWorkTests extends AbstractEventBusTest {

  private CommandDispatcher commandDispatcher;
  private EventPublisher eventPublisher;
  private TestCommandHandler commandHandler;
  private TestingUnitOfWorkFactory unitOfWorkFactory;
  private TestEventHandler eventHandler;

  @BeforeEach
  void setUp() throws Exception {
    unitOfWorkFactory = new TestingUnitOfWorkFactory();
    SynchronousEventBus eventBus = createSynchronousEventBus(Collections.emptyList(), unitOfWorkFactory);

    commandHandler = new TestCommandHandler();
    eventBus.subscribe(commandHandler);
    eventHandler = new TestEventHandler();
    eventBus.subscribe(eventHandler);

    commandDispatcher = eventBus;
    eventPublisher = eventBus;
  }

  @Test
  void whenDispatching_shouldCreateAUnitOfWork() {
    // given
    HelloCommand helloCommand = new HelloCommand("Domenique");

    // given
    commandDispatcher.dispatch(helloCommand);

    // then
    assertThat(commandHandler.isHelloCommandHandled, is(true));
    assertThat(unitOfWorkFactory.getCreatedUoW(), notNullValue());
    assertThat(unitOfWorkFactory.getCreatedUoW().isCommitCalled, is(true));
  }

  @Test
  void whenAUnitOfWorkExists_eventsShouldBeStoredInAUnitOfWork() {
    // given
    TestEvent testEvent = new TestEvent();
    TestUnitOfWork unitOfWork = new TestUnitOfWork();
    UnitOfWorkRepository.store(unitOfWork);

    // when
    eventPublisher.publish(testEvent);

    // then
    assertThat(eventHandler.testEventHandled, is(false));
    assertThat(unitOfWork.isCommitCalled, is(false));
    assertThat(unitOfWork.delayedEvents, hasItem(sameInstance(testEvent)));
  }

}
