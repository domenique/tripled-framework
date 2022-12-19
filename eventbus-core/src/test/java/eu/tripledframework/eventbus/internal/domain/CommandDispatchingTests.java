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

import eu.tripledframework.eventbus.CommandCallback;
import eu.tripledframework.eventbus.CommandDispatcher;
import eu.tripledframework.eventbus.EventSubscriber;
import eu.tripledframework.eventbus.command.AnCommandHandledByMultipleHandlers;
import eu.tripledframework.eventbus.command.CommandHandledByAPrivateMethod;
import eu.tripledframework.eventbus.command.FailingCommand;
import eu.tripledframework.eventbus.command.FailingCommandWithCheckedException;
import eu.tripledframework.eventbus.command.HelloCommand;
import eu.tripledframework.eventbus.command.UnhandledCommand;
import eu.tripledframework.eventbus.command.ValidatingCommand;
import eu.tripledframework.eventbus.handler.SecondTestCommandHandler;
import eu.tripledframework.eventbus.handler.TestCommandHandler;
import eu.tripledframework.eventbus.internal.infrastructure.callback.ExceptionThrowingCommandCallback;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.LoggingEventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.DuplicateInvokerFoundException;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InvocationException;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InvokerNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class CommandDispatchingTests extends AbstractEventBusTest {
    private CommandDispatcher commandDispatcher;
    private TestCommandHandler eventHandler;

    @BeforeEach
    void setUp() throws Exception {
      var eventBus = createSynchronousEventBus(Collections.singletonList(new LoggingEventBusInterceptor()));

        eventHandler = new TestCommandHandler();
        eventBus.subscribe(eventHandler);

        commandDispatcher = eventBus;
    }

    @Test
    void whenNotGivingAnyInterceptors_shouldBeAbleToExecuteCommand() throws Exception {
        // given
      var helloCommand = new HelloCommand("Domenique");
      var publisherWithoutInterceptors = createSynchronousEventBus(Collections.emptyList());
      var myEventHandler = new TestCommandHandler();
        publisherWithoutInterceptors.subscribe(myEventHandler);

        // when
        publisherWithoutInterceptors.dispatch(helloCommand);

        // then
        assertThat(myEventHandler.isHelloCommandHandled, is(true));
    }

  @Test
  void whenGivingIncomingInterceptors_shouldBeAbleToExecuteCommand() throws Exception {
    // given
    var helloCommand = new HelloCommand("Domenique");
    var publisherWithoutInterceptors = createSynchronousEventBus(Collections.emptyList(), Collections.singletonList(new LoggingEventBusInterceptor()));
    var myEventHandler = new TestCommandHandler();
    publisherWithoutInterceptors.subscribe(myEventHandler);

    // when
    publisherWithoutInterceptors.dispatch(helloCommand);

    // then
    assertThat(myEventHandler.isHelloCommandHandled, is(true));
  }

    @Test
    void whenGivenAHelloCommand_shouldCallEventHandler() throws Exception {
        // given
      var helloCommand = new HelloCommand("Domenique");

        // given
        commandDispatcher.dispatch(helloCommand);

        // then
        assertThat(eventHandler.isHelloCommandHandled, is(true));
    }

    @Test
    void whenGivenCommandThatSucceeds_shouldInvokeCallback() throws Exception {
        // given
      var validatingCommand = new ValidatingCommand("message");

        // when
        commandDispatcher.dispatch(validatingCommand, new CommandCallback<Void>() {
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

    @Test
    void whenGivenAHelloCommandAndCallback_shouldBeAbleToRetrieveResponse() throws Exception {
        // given
      var helloCommand = new HelloCommand("Domenique");
      var callback = new ExceptionThrowingCommandCallback<String>();

        // given
        commandDispatcher.dispatch(helloCommand, callback);

        // then
        assertThat(callback.getResult(), equalTo("Hello Domenique"));
    }


    @Test
    void whenGivenACommandThatFails_exceptionShouldBeThrown() throws Exception {
        // given
      var command = new FailingCommand();

        // when
        Assertions.assertThrows(IllegalStateException.class, () -> commandDispatcher.dispatch(command, new ExceptionThrowingCommandCallback<>()));

        // then --> exception
    }

    @Test
    void whenGivenACommandWhichFails_shouldFail() throws Exception {
        // given
      var command = new FailingCommand();

        // when
        Future<Void> future = commandDispatcher.dispatch(command);

        try {
            future.get();
        } catch (ExecutionException ex) {
            assertThat(future.isDone(), is(true));
            assertThat(ex, instanceOf(ExecutionException.class));
            assertThat(eventHandler.isFailingCommandHandled, is(true));
        }
    }

    @Test
    void whenGivenACommandWhichFailsWithACheckedExceptionUsingAFuture_shouldFail() throws Exception {
        // given
      var command = new FailingCommandWithCheckedException();

        // when
        Future<Void> future = commandDispatcher.dispatch(command);

        try {
            future.get();
        } catch (Exception ex) {
            assertThat(future.isDone(), is(true));
            assertThat(ex, instanceOf(ExecutionException.class));
            assertThat(ex.getCause(), instanceOf(InvocationException.class));
            assertThat(eventHandler.isFailingCommandHandled, is(true));
        }
    }

    @Test
    void whenGivenACommandWhichIsHandledByAPrivateMethod_shouldFailWithHandlerNotFound() throws Exception {
        // given
      var command = new CommandHandledByAPrivateMethod();

        // when
        try {
            commandDispatcher.dispatch(command);
        } catch (Exception ex) {
            assertThat(ex, instanceOf(InvokerNotFoundException.class));
            assertThat(eventHandler.isCommandHandledByAPrivateMethodCalled, is(false));
        }
    }

    @Test
    void whenGivenCommandForWhichNoHandlerExists_shouldThrowException() throws Exception {
        // given
      var command = new UnhandledCommand();
        Assertions.assertThrows(InvokerNotFoundException.class, () -> commandDispatcher.dispatch(command));

        // when

        // then --> exception
    }

    @Test
    void whenRegisteringDuplicateEventHandlerWithReturnType_shouldNotFail() throws Exception {
        // given
      var secondEventHandler = new SecondTestCommandHandler();

        // when
        ((EventSubscriber) commandDispatcher).subscribe(secondEventHandler);

        // then
        // TODO: How can we assert that the subscription worked?

    }

    @Test
    void whenRegisteringADuplicateEventHandler_shouldNotInvokeAny() {
        // given
      var command = new AnCommandHandledByMultipleHandlers();
        ((EventSubscriber) commandDispatcher).subscribe(eventHandler);

        // when
        Assertions.assertThrows(DuplicateInvokerFoundException.class, () -> commandDispatcher.dispatch(command));

        // then -> exception
    }
}
