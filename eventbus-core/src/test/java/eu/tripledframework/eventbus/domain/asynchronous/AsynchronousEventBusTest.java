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

package eu.tripledframework.eventbus.domain.asynchronous;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import eu.tripledframework.eventbus.command.HelloCommand;
import eu.tripledframework.eventbus.command.ValidatingCommand;
import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.EventPublisher;
import eu.tripledframework.eventbus.domain.interceptor.TestValidator;
import eu.tripledframework.eventbus.domain.interceptor.ValidatingEventBusInterceptor;
import eu.tripledframework.eventbus.handler.TestEventHandler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class AsynchronousEventBusTest {

  public static final String THREAD_POOL_PREFIX = "CDForTest-pool-";
  public static final String THREAD_POOL_WITH_VALIDATION_PREFIX = "CDForTest-Val-pool-";
  private EventPublisher asynchronousDispatcher;
  private EventPublisher dispatcherWithValidation;
  private TestEventHandler eventHandler;
  private TestValidator validator;

  @Before
  public void setUp() throws Exception {
    eventHandler = new TestEventHandler();
    ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
        .setNameFormat(THREAD_POOL_PREFIX + "%d")
        .build());
    AsynchronousEventBus eventBus = new AsynchronousEventBus(executor);
    eventBus.subscribe(eventHandler);
    asynchronousDispatcher = eventBus;

    ExecutorService executor2 = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
        .setNameFormat(THREAD_POOL_WITH_VALIDATION_PREFIX + "%d")
        .build());
    validator = new TestValidator();
    List<EventBusInterceptor> interceptors = Arrays.asList(new ValidatingEventBusInterceptor(validator));
    AsynchronousEventBus eventBus2 = new AsynchronousEventBus(interceptors, executor2);
    eventBus2.subscribe(eventHandler);
    dispatcherWithValidation = eventBus2;
  }

  @Test
  public void whenNotGivenAnExecutor_shouldCreateADefaultOneAndExecuteCommands() throws Exception {
    // given
    AsynchronousEventBus defaultPublisher = new AsynchronousEventBus();
    TestEventHandler eventHandler = new TestEventHandler();
    defaultPublisher.subscribe(eventHandler);
    HelloCommand command = new HelloCommand("domenique");

    // when
    Future<Void> future = defaultPublisher.publish(command);
    future.get();

    // then
    assertThat(future.isDone(), is(true));
    assertThat(eventHandler.isHelloCommandHandled, is(true));
  }

  @Test
  public void whenGivenAValidCommand_shouldBeExecutedAsynchronous() throws Exception {
    // given
    HelloCommand command = new HelloCommand("Domenique");

    // when
    Future<Void> future = asynchronousDispatcher.publish(command);
    future.get();

    // then
    assertThat(eventHandler.isHelloCommandHandled, is(true));
    assertThat(eventHandler.threadNameForExecute, equalTo(THREAD_POOL_PREFIX + "0"));
  }

  @Test
  public void whenGivenAValidCommandAndFutureCallback_waitForExecutionToFinish() throws Exception {
    // given
    HelloCommand command = new HelloCommand("Domenique");

    // when
    Future<Void> future = asynchronousDispatcher.publish(command);
    future.get();

    // then
    assertThat(future.isDone(), is(true));
    assertThat(eventHandler.isHelloCommandHandled, is(true));
    assertThat(eventHandler.threadNameForExecute, equalTo(THREAD_POOL_PREFIX + "0"));
  }

  @Test
  public void whenUsingADispatcherWithValidator_validationShouldBeDoneAsynchronous() throws Exception {
    // given
    ValidatingCommand command = new ValidatingCommand("should pass");
    validator.shouldFailNextCall(false);

    // when
    Future<Void> future = dispatcherWithValidation.publish(command);
    future.get();

    // then
    assertThat(future.isDone(), is(true));
    assertThat(eventHandler.isValidatingCommandHandled, is(true));
    assertThat(eventHandler.threadNameForExecute, equalTo(THREAD_POOL_WITH_VALIDATION_PREFIX + "0"));
  }
}