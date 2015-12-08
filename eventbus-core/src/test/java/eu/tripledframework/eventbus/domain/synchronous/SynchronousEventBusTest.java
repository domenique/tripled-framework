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
package eu.tripledframework.eventbus.domain.synchronous;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import eu.tripledframework.eventbus.command.ACommandHandledByMultipleHandlers;
import eu.tripledframework.eventbus.command.CommandHandledByAPrivateMethod;
import eu.tripledframework.eventbus.command.FailingCommand;
import eu.tripledframework.eventbus.command.FailingCommandWithCheckedException;
import eu.tripledframework.eventbus.command.HelloCommand;
import eu.tripledframework.eventbus.command.UnhandledCommand;
import eu.tripledframework.eventbus.command.ValidatingCommand;
import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.EventCallback;
import eu.tripledframework.eventbus.domain.EventPublisher;
import eu.tripledframework.eventbus.domain.EventSubscriber;
import eu.tripledframework.eventbus.domain.callback.ExceptionThrowingEventCallback;
import eu.tripledframework.eventbus.domain.dispatcher.EventHandlerNotFoundException;
import eu.tripledframework.eventbus.domain.interceptor.CommandValidationException;
import eu.tripledframework.eventbus.domain.interceptor.LoggingEventBusInterceptor;
import eu.tripledframework.eventbus.domain.interceptor.TestValidator;
import eu.tripledframework.eventbus.domain.interceptor.ValidatingEventBusInterceptor;
import eu.tripledframework.eventbus.domain.invoker.DuplicateEventHandlerRegistrationException;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvocationException;
import eu.tripledframework.eventbus.domain.invoker.TestEventHandlerInvokerFactory;
import eu.tripledframework.eventbus.handler.SecondTestEventHandler;
import eu.tripledframework.eventbus.handler.TestEventHandler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


public class SynchronousEventBusTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private EventPublisher eventPublisher;
  private TestEventHandler eventHandler;
  private TestValidator validator;

  @Before
  public void setUp() throws Exception {
    List<EventBusInterceptor> interceptors = new ArrayList<>();
    interceptors.add(0, new LoggingEventBusInterceptor());
    validator = new TestValidator();
    interceptors.add(1, new ValidatingEventBusInterceptor(validator));

    SynchronousEventBus eventBus = new SynchronousEventBus(interceptors);

    eventHandler = new TestEventHandler();
    eventBus.subscribe(eventHandler);

    eventPublisher = eventBus;
  }

  @Test
  public void whenGivenAHelloCommand_shouldCallEventHandler() throws Exception {
    // given
    HelloCommand helloCommand = new HelloCommand("Domenique");

    // given
    eventPublisher.publish(helloCommand);

    // then
    assertThat(eventHandler.isHelloCommandHandled, is(true));
  }

  @Test
  public void whenGivenAHelloCommandAndCallback_shouldBeAbleToRetrieveResponse() throws Exception {
    // given
    HelloCommand helloCommand = new HelloCommand("Domenique");
    ExceptionThrowingEventCallback<String> callback = new ExceptionThrowingEventCallback<>();

    // given
    eventPublisher.publish(helloCommand, callback);

    // then
    assertThat(callback.getResult(), equalTo("Hello Domenique"));
  }

  @Test
  public void whenNotGivingAnyInterceptors_shouldExecuteCommand() throws Exception {
    // given
    HelloCommand helloCommand = new HelloCommand("Domenique");
    SynchronousEventBus publisherWithoutInterceptors = new SynchronousEventBus();
    TestEventHandler myEventHandler = new TestEventHandler();
    publisherWithoutInterceptors.subscribe(myEventHandler);

    // when
    publisherWithoutInterceptors.publish(helloCommand);

    // then
    assertThat(myEventHandler.isHelloCommandHandled, is(true));
  }

  @Test
  public void whenGivenAnEventHandlerInvokerFactory_shouldUseIt() throws Exception {
    // given
    SynchronousEventBus eventBus = new SynchronousEventBus();
    TestEventHandlerInvokerFactory invokerFactory = new TestEventHandlerInvokerFactory();
    eventBus.setEventHandlerInvokerFactory(Arrays.asList(invokerFactory));

    // when
    eventBus.subscribe(new TestEventHandler());

    // then
    assertThat(invokerFactory.isCreateCalled, is(true));
  }

  @Test(expected = IllegalArgumentException.class)
  public void whenGivenAnEmptyEventHandlerInvokerFactoryList_shouldFail() throws Exception {
    // given
    SynchronousEventBus eventBus = new SynchronousEventBus();

    // when
    eventBus.setEventHandlerInvokerFactory(Collections.emptyList());

    // then -> exception
  }

  @Test(expected = IllegalArgumentException.class)
  public void whenGivenANullEventHandlerInvokerFactoryList_shouldFail() throws Exception {
    // given
    SynchronousEventBus eventBus = new SynchronousEventBus();

    // when
    eventBus.setEventHandlerInvokerFactory(null);

    // then -> exception
  }

  @Test(expected = IllegalStateException.class)
  public void whenGivenACommandThatFails_exceptionShouldBeThrown() throws Exception {
    // given
    FailingCommand command = new FailingCommand();

    // when
    eventPublisher.publish(command, new ExceptionThrowingEventCallback<>());

    // then --> exception
  }

  @Test
  public void whenGivenCommandThatFailsValidation_shouldInvokeCallback() throws Exception {
    // given
    ValidatingCommand validatingCommand = new ValidatingCommand(null);
    validator.shouldFailNextCall(true);

    // when
    eventPublisher.publish(validatingCommand, new EventCallback<Void>() {
      @Override
      public void onSuccess(Void result) {
        fail("onSuccess should not be called.");
      }

      @Override
      public void onFailure(RuntimeException exception) {
        if (!(exception instanceof CommandValidationException)) {
          fail("onFailure should not be called.");
        }
      }
    });

    assertThat(eventHandler.isValidatingCommandHandled, is(false));
  }

  @Test
  public void whenGivenCommandThatFailsValidation_shouldThrowException() throws Exception {
    // given
    ValidatingCommand validatingCommand = new ValidatingCommand(null);
    validator.shouldFailNextCall(true);

    // when
    try {
      eventPublisher.publish(validatingCommand);
    } catch (CommandValidationException ex) {
      // then --> exception
      assertThat(ex.getConstraintViolations().size(), is(1));
    }

  }

  @Test
  public void whenGivenACommandWhichFails_shouldFail() throws Exception {
    // given
    FailingCommand command = new FailingCommand();

    // when
    Future<Void> future = eventPublisher.publish(command);

    try {
      future.get();
    } catch (ExecutionException ex) {
      assertThat(future.isDone(), is(true));
      assertThat(ex, instanceOf(ExecutionException.class));
      assertThat(eventHandler.isFailingCommandHandled, is(true));
    }
  }

  @Test
  public void whenGivenACommandWhichFailsWithACheckedExceptionUsingAFuture_shouldFail() throws Exception {
    // given
    FailingCommandWithCheckedException command = new FailingCommandWithCheckedException();

    // when
    Future<Void> future = eventPublisher.publish(command);

    try {
      future.get();
    } catch (Exception ex) {
      assertThat(future.isDone(), is(true));
      assertThat(ex, instanceOf(ExecutionException.class));
      assertThat(ex.getCause(), instanceOf(EventHandlerInvocationException.class));
      assertThat(eventHandler.isFailingCommandHandled, is(true));
    }
  }

  @Test
  public void whenGivenACommandWhichFailsWithACheckedExceptionUsing_shouldFail() throws Exception {
    // given
    FailingCommandWithCheckedException command = new FailingCommandWithCheckedException();

    // when
    try {
      eventPublisher.publish(command);
    } catch (Exception ex) {
      assertThat(ex, instanceOf(EventHandlerInvocationException.class));
      assertThat(eventHandler.isFailingCommandHandled, is(true));
    }
  }

  @Test
  public void whenGivenACommandWhichIsHandledByAPrivateMethod_shouldFail() throws Exception {
    // given
    CommandHandledByAPrivateMethod command = new CommandHandledByAPrivateMethod();

    // when
    try {
      eventPublisher.publish(command);
    } catch (Exception ex) {
      assertThat(ex, instanceOf(EventHandlerInvocationException.class));
      assertThat(eventHandler.isCommandHandledByAPrivateMethodCalled, is(false));
    }
  }

  @Test
  public void whenGivenCommandThatSucceeds_shouldInvokeCallback() throws Exception {
    // given
    ValidatingCommand validatingCommand = new ValidatingCommand("message");
    validator.shouldFailNextCall(false);

    // when
    eventPublisher.publish(validatingCommand, new EventCallback<Void>() {
      @Override
      public void onSuccess(Void result) {
        assertThat(result, nullValue());
      }

      @Override
      public void onFailure(RuntimeException exception) {
        fail("onFailure should not be called.");
      }
    });

    assertThat(eventHandler.isValidatingCommandHandled, is(true));
  }

  @Test(expected = EventHandlerNotFoundException.class)
  public void whenGivenCommandForWhichNoHandlerExists_shouldThrowException() throws Exception {
    // given
    UnhandledCommand command = new UnhandledCommand();

    // when
    eventPublisher.publish(command);

    // then --> exception
  }

  @Test(expected = DuplicateEventHandlerRegistrationException.class)
  public void whenRegisteringDuplicateEventHandlerWithReturnType_shouldFailWithException() throws Exception {
    // given
    SecondTestEventHandler secondEventHandler = new SecondTestEventHandler();

    // when
    ((EventSubscriber) eventPublisher).subscribe(secondEventHandler);

    // then --> exception
  }

  @Test
  public void whenGivenACommandWithMultipleHandlers_allHandlersShouldBeInvoked() throws Exception {
    // given
    ACommandHandledByMultipleHandlers command = new ACommandHandledByMultipleHandlers();

    // when
    eventPublisher.publish(command);

    // then
    assertThat(eventHandler.handledByFirstCount, equalTo(1));
    assertThat(eventHandler.handledBySecondCount, equalTo(1));
  }

  @Test
  public void whenRegisteringADuplicateEventHandler_shouldNotInvokeTwice() throws Exception {
    // given
    ACommandHandledByMultipleHandlers command = new ACommandHandledByMultipleHandlers();
    ((EventSubscriber) eventPublisher).subscribe(eventHandler);

    // when
    eventPublisher.publish(command);

    // then
    assertThat(eventHandler.handledByFirstCount, equalTo(1));
    assertThat(eventHandler.handledBySecondCount, equalTo(1));


  }
}