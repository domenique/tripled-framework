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
import eu.tripledframework.eventbus.EventBusBuilder;
import eu.tripledframework.eventbus.command.HelloCommand;
import eu.tripledframework.eventbus.command.ValidatingCommand;
import eu.tripledframework.eventbus.handler.TestCommandHandler;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.TestValidator;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.ValidatingEventBusInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class AsynchronousEventBusTest {

  private static final String THREAD_POOL_NAME = "CDForTest-pool";
  private static final String THREAD_POOL_WITH_VALIDATION_NAME = "CDForTest-Val-pool";
  private CommandDispatcher asynchronousDispatcher;
  private CommandDispatcher dispatcherWithValidation;
  private TestCommandHandler eventHandler;
  private TestValidator validator;

  @BeforeEach
  void setUp() {
    eventHandler = new TestCommandHandler();
    var eventBus = EventBusBuilder.newBuilder()
            .withExecutor(Executors.newCachedThreadPool(r -> new Thread(r, THREAD_POOL_NAME)))
            .buildASynchronousEventBus();

    eventBus.subscribe(eventHandler);
    asynchronousDispatcher = eventBus;

    validator = new TestValidator();
    var eventBus2 = EventBusBuilder.newBuilder()
            .withExecutor(Executors.newCachedThreadPool(r -> new Thread(r, THREAD_POOL_WITH_VALIDATION_NAME)))
            .withInvokerInterceptors(List.of(new ValidatingEventBusInterceptor(validator)))
            .buildASynchronousEventBus();

    eventBus2.subscribe(eventHandler);
    dispatcherWithValidation = eventBus2;
  }

  @Test
  void whenNotGivenAnExecutor_shouldCreateADefaultOneAndExecuteCommands() throws Exception {
    // given
    var defaultPublisher = EventBusBuilder.newBuilder()
            .buildASynchronousEventBus();
    var eventHandler = new TestCommandHandler();
    defaultPublisher.subscribe(eventHandler);
    var command = new HelloCommand("domenique");

    // when
    Future<Void> future = defaultPublisher.dispatch(command);
    future.get();

    // then
    assertThat(future.isDone(), is(true));
    assertThat(eventHandler.isHelloCommandHandled, is(true));
  }

  @Test
  void whenGivenAValidCommand_shouldBeExecutedAsynchronous() throws Exception {
    // given
    var command = new HelloCommand("Domenique");

    // when
    Future<Void> future = asynchronousDispatcher.dispatch(command);
    future.get();

    // then
    assertThat(eventHandler.isHelloCommandHandled, is(true));
    assertThat(eventHandler.threadNameForExecute, equalTo(THREAD_POOL_NAME));
  }

  @Test
  void whenGivenAValidCommandAndFutureCallback_waitForExecutionToFinish() throws Exception {
    // given
    var command = new HelloCommand("Domenique");

    // when
    Future<Void> future = asynchronousDispatcher.dispatch(command);
    future.get();

    // then
    assertThat(future.isDone(), is(true));
    assertThat(eventHandler.isHelloCommandHandled, is(true));
    assertThat(eventHandler.threadNameForExecute, equalTo(THREAD_POOL_NAME));
  }

  @Test
  void whenUsingADispatcherWithValidator_validationShouldBeDoneAsynchronous() throws Exception {
    // given
    var command = new ValidatingCommand("should pass");
    validator.shouldFailNextCall(false);

    // when
    Future<Void> future = dispatcherWithValidation.dispatch(command);
    future.get();

    // then
    assertThat(future.isDone(), is(true));
    assertThat(eventHandler.isValidatingCommandHandled, is(true));
    assertThat(eventHandler.threadNameForExecute, equalTo(THREAD_POOL_WITH_VALIDATION_NAME));
  }
}