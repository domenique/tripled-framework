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
import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.command.HelloCommand;
import eu.tripledframework.eventbus.command.ValidatingCommand;
import eu.tripledframework.eventbus.handler.TestCommandHandler;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.SimpleInterceptorChainFactory;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.TestValidator;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.ValidatingEventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InMemoryInvokerRepository;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.SimpleInvokerFactory;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.DefaultUnitOfWorkFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class AsynchronousEventBusTest {

  private static final String THREAD_POOL_PREFIX = "CDForTest-pool-";
  private static final String THREAD_POOL_WITH_VALIDATION_PREFIX = "CDForTest-Val-pool-";
  private CommandDispatcher asynchronousDispatcher;
  private CommandDispatcher dispatcherWithValidation;
  private TestCommandHandler eventHandler;
  private TestValidator validator;

  @BeforeEach
  void setUp() throws Exception {
    eventHandler = new TestCommandHandler();
    ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory(THREAD_POOL_PREFIX));
    AsynchronousEventBus eventBus = new AsynchronousEventBus(new InMemoryInvokerRepository(),
        new SimpleInterceptorChainFactory(), Collections
        .singletonList(new SimpleInvokerFactory()), new DefaultUnitOfWorkFactory(), executor);
    eventBus.subscribe(eventHandler);
    asynchronousDispatcher = eventBus;

    ExecutorService executor2 = Executors.newCachedThreadPool(new NamedThreadFactory(THREAD_POOL_WITH_VALIDATION_PREFIX));
    validator = new TestValidator();
    List<EventBusInterceptor> interceptors = Collections.singletonList(new ValidatingEventBusInterceptor(validator));
    AsynchronousEventBus eventBus2 = new AsynchronousEventBus(new InMemoryInvokerRepository(),
        new SimpleInterceptorChainFactory(interceptors), Collections
        .singletonList(new SimpleInvokerFactory()), new DefaultUnitOfWorkFactory(), executor2);
    eventBus2.subscribe(eventHandler);
    dispatcherWithValidation = eventBus2;
  }

  @Test
  void whenNotGivenAnExecutor_shouldCreateADefaultOneAndExecuteCommands() throws Exception {
    // given
    AsynchronousEventBus defaultPublisher = new AsynchronousEventBus(new InMemoryInvokerRepository(),
        new SimpleInterceptorChainFactory(), Collections
        .singletonList(new SimpleInvokerFactory()), new DefaultUnitOfWorkFactory());
    TestCommandHandler eventHandler = new TestCommandHandler();
    defaultPublisher.subscribe(eventHandler);
    HelloCommand command = new HelloCommand("domenique");

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
    HelloCommand command = new HelloCommand("Domenique");

    // when
    Future<Void> future = asynchronousDispatcher.dispatch(command);
    future.get();

    // then
    assertThat(eventHandler.isHelloCommandHandled, is(true));
    assertThat(eventHandler.threadNameForExecute, equalTo(THREAD_POOL_PREFIX + "0"));
  }

  @Test
  void whenGivenAValidCommandAndFutureCallback_waitForExecutionToFinish() throws Exception {
    // given
    HelloCommand command = new HelloCommand("Domenique");

    // when
    Future<Void> future = asynchronousDispatcher.dispatch(command);
    future.get();

    // then
    assertThat(future.isDone(), is(true));
    assertThat(eventHandler.isHelloCommandHandled, is(true));
    assertThat(eventHandler.threadNameForExecute, equalTo(THREAD_POOL_PREFIX + "0"));
  }

  @Test
  void whenUsingADispatcherWithValidator_validationShouldBeDoneAsynchronous() throws Exception {
    // given
    ValidatingCommand command = new ValidatingCommand("should pass");
    validator.shouldFailNextCall(false);

    // when
    Future<Void> future = dispatcherWithValidation.dispatch(command);
    future.get();

    // then
    assertThat(future.isDone(), is(true));
    assertThat(eventHandler.isValidatingCommandHandled, is(true));
    assertThat(eventHandler.threadNameForExecute, equalTo(THREAD_POOL_WITH_VALIDATION_PREFIX + "0"));
  }
}