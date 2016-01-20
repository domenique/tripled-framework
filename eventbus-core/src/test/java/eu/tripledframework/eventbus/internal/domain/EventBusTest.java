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
package eu.tripledframework.eventbus.internal.domain;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import eu.tripledframework.eventbus.CommandCallback;
import eu.tripledframework.eventbus.CommandDispatcher;
import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.EventPublisher;
import eu.tripledframework.eventbus.EventSubscriber;
import eu.tripledframework.eventbus.command.CommandHandledByAPrivateMethod;
import eu.tripledframework.eventbus.command.FailingCommand;
import eu.tripledframework.eventbus.command.FailingCommandWithCheckedException;
import eu.tripledframework.eventbus.command.HelloCommand;
import eu.tripledframework.eventbus.command.UnhandledCommand;
import eu.tripledframework.eventbus.command.ValidatingCommand;
import eu.tripledframework.eventbus.command.AnCommandHandledByMultipleHandlers;
import eu.tripledframework.eventbus.event.TestEvent;
import eu.tripledframework.eventbus.event.UnhandledEvent;
import eu.tripledframework.eventbus.handler.SecondTestCommandHandler;
import eu.tripledframework.eventbus.handler.TestCommandHandler;
import eu.tripledframework.eventbus.handler.TestEventHandler;
import eu.tripledframework.eventbus.internal.infrastructure.callback.ExceptionThrowingCommandCallback;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.CommandValidationException;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.LoggingEventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.SimpleInterceptorChainFactory;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.TestValidator;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.ValidatingEventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.DuplicateInvokerFoundException;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InMemoryInvokerRepository;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InvocationException;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InvokerNotFoundException;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.SimpleInvokerFactory;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.TestInvokerFactory;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.DefaultUnitOfWorkFactory;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.TestUnitOfWork;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.TestingUnitOfWorkFactory;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.UnitOfWorkManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(HierarchicalContextRunner.class)
public class EventBusTest {

  @Test
  public void whenNotGivingAnyInterceptors_shouldBeAbleToExecuteCommand() throws Exception {
    // given
    HelloCommand helloCommand = new HelloCommand("Domenique");
    SynchronousEventBus publisherWithoutInterceptors = createSynchronousEventBus(Collections.emptyList());
    TestCommandHandler myEventHandler = new TestCommandHandler();
    publisherWithoutInterceptors.subscribe(myEventHandler);

    // when
    publisherWithoutInterceptors.dispatch(helloCommand);

    // then
    assertThat(myEventHandler.isHelloCommandHandled, is(true));
  }

  @Test
  public void whenGivenAnEventHandlerInvokerFactory_shouldUseIt() throws Exception {
    // given
    TestInvokerFactory invokerFactory = new TestInvokerFactory();
    SynchronousEventBus eventBus = createSynchronousEventBus(Collections.emptyList(), Collections.singletonList(invokerFactory));

    // when
    eventBus.subscribe(new TestCommandHandler());

    // then
    assertThat(invokerFactory.isCreateCalled, is(true));
  }

  public class CommandDispatchingTests {
    private CommandDispatcher commandDispatcher;
    private TestCommandHandler eventHandler;

    @Before
    public void setUp() throws Exception {
      SynchronousEventBus eventBus = createSynchronousEventBus(Collections.singletonList(new LoggingEventBusInterceptor()));

      eventHandler = new TestCommandHandler();
      eventBus.subscribe(eventHandler);

      commandDispatcher = eventBus;
    }

    @Test
    public void whenGivenAHelloCommand_shouldCallEventHandler() throws Exception {
      // given
      HelloCommand helloCommand = new HelloCommand("Domenique");

      // given
      commandDispatcher.dispatch(helloCommand);

      // then
      assertThat(eventHandler.isHelloCommandHandled, is(true));
    }

    @Test
    public void whenGivenCommandThatSucceeds_shouldInvokeCallback() throws Exception {
      // given
      ValidatingCommand validatingCommand = new ValidatingCommand("message");

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
    public void whenGivenAHelloCommandAndCallback_shouldBeAbleToRetrieveResponse() throws Exception {
      // given
      HelloCommand helloCommand = new HelloCommand("Domenique");
      ExceptionThrowingCommandCallback<String> callback = new ExceptionThrowingCommandCallback<>();

      // given
      commandDispatcher.dispatch(helloCommand, callback);

      // then
      assertThat(callback.getResult(), equalTo("Hello Domenique"));
    }


    @Test(expected = IllegalStateException.class)
    public void whenGivenACommandThatFails_exceptionShouldBeThrown() throws Exception {
      // given
      FailingCommand command = new FailingCommand();

      // when
      commandDispatcher.dispatch(command, new ExceptionThrowingCommandCallback<>());

      // then --> exception
    }

    @Test
    public void whenGivenACommandWhichFails_shouldFail() throws Exception {
      // given
      FailingCommand command = new FailingCommand();

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
    public void whenGivenACommandWhichFailsWithACheckedExceptionUsingAFuture_shouldFail() throws Exception {
      // given
      FailingCommandWithCheckedException command = new FailingCommandWithCheckedException();

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
    public void whenGivenACommandWhichIsHandledByAPrivateMethod_shouldFailWithHandlerNotFound() throws Exception {
      // given
      CommandHandledByAPrivateMethod command = new CommandHandledByAPrivateMethod();

      // when
      try {
        commandDispatcher.dispatch(command);
      } catch (Exception ex) {
        assertThat(ex, instanceOf(InvokerNotFoundException.class));
        assertThat(eventHandler.isCommandHandledByAPrivateMethodCalled, is(false));
      }
    }

    @Test(expected = InvokerNotFoundException.class)
    public void whenGivenCommandForWhichNoHandlerExists_shouldThrowException() throws Exception {
      // given
      UnhandledCommand command = new UnhandledCommand();

      // when
      commandDispatcher.dispatch(command);

      // then --> exception
    }

    @Test
    public void whenRegisteringDuplicateEventHandlerWithReturnType_shouldNotFail() throws Exception {
      // given
      SecondTestCommandHandler secondEventHandler = new SecondTestCommandHandler();

      // when
      ((EventSubscriber) commandDispatcher).subscribe(secondEventHandler);

      // then
      // TODO: How can we assert that the subscription worked?

    }

    @Test(expected = DuplicateInvokerFoundException.class)
    public void whenRegisteringADuplicateEventHandler_shouldNotInvokeAny() throws Exception {
      // given
      AnCommandHandledByMultipleHandlers command = new AnCommandHandledByMultipleHandlers();
      ((EventSubscriber) commandDispatcher).subscribe(eventHandler);

      // when
      commandDispatcher.dispatch(command);

      // then -> exception
    }

  }

  public class EventPublishingTests {

    private EventPublisher eventPublisher;
    private TestCommandHandler eventHandler;

    @Before
    public void setUp() throws Exception {
      SynchronousEventBus eventBus = createSynchronousEventBus(Collections.singletonList(new LoggingEventBusInterceptor()));

      eventHandler = new TestCommandHandler();
      eventBus.subscribe(eventHandler);

      eventPublisher = eventBus;
    }

    @Test
    public void whenGivenAnEventWithMultipleHandlers_allHandlersShouldBeInvoked() throws Exception {
      // given
      AnCommandHandledByMultipleHandlers event = new AnCommandHandledByMultipleHandlers();

      // when
      eventPublisher.publish(event);

      // then
      assertThat(eventHandler.handledByFirstCount, equalTo(1));
      assertThat(eventHandler.handledBySecondCount, equalTo(1));
    }

    @Test(expected = InvokerNotFoundException.class)
    public void whenGivenAnEventWithNoHandlers_shouldThrowException() throws Exception {
      // given
      UnhandledEvent unhandledEvent = new UnhandledEvent();

      // when
      eventPublisher.publish(unhandledEvent);

      // then -> exception
    }

  }

  public class CommandDispatchingWithValidationTests {

    private CommandDispatcher commandDispatcher;
    private TestCommandHandler eventHandler;
    private TestValidator validator;

    @Before
    public void setUp() throws Exception {
      validator = new TestValidator();
      List<EventBusInterceptor> interceptors = new ArrayList<>();
      interceptors.add(0, new LoggingEventBusInterceptor());
      interceptors.add(1, new ValidatingEventBusInterceptor(validator));

      SynchronousEventBus eventBus = createSynchronousEventBus(interceptors);

      eventHandler = new TestCommandHandler();
      eventBus.subscribe(eventHandler);

      commandDispatcher = eventBus;
    }

    @Test
    public void whenGivenCommandThatFailsValidation_shouldInvokeCallback() throws Exception {
      // given
      ValidatingCommand validatingCommand = new ValidatingCommand(null);
      validator.shouldFailNextCall(true);

      // when
      commandDispatcher.dispatch(validatingCommand, new CommandCallback<Void>() {
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
        commandDispatcher.dispatch(validatingCommand);
      } catch (CommandValidationException ex) {
        // then --> exception
        assertThat(ex.getConstraintViolations().size(), is(1));
      }

    }
  }

  public class CommandDispatchingUnitOfWorkTests {

    private CommandDispatcher commandDispatcher;
    private EventPublisher eventPublisher;
    private TestCommandHandler commandHandler;
    private TestingUnitOfWorkFactory unitOfWorkFactory;
    private TestEventHandler eventHandler;

    @Before
    public void setUp() throws Exception {
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
    public void whenDispatching_shouldCreateAUnitOfWork() {
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
    public void whenAUnitOfWorkExists_eventsShouldBeStoredInAUnitOfWork() {
      // given
      TestEvent testEvent = new TestEvent();
      TestUnitOfWork unitOfWork = new TestUnitOfWork();
      UnitOfWorkManager.store(unitOfWork);

      // when
      eventPublisher.publish(testEvent);

      // then
      assertThat(eventHandler.testEventHandled, is(false));
      assertThat(unitOfWork.isCommitCalled, is(false));
      assertThat(unitOfWork.delayedEvents, hasItem(sameInstance(testEvent)));
    }

  }

  private SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> interceptors) {
    return new SynchronousEventBus(new InMemoryInvokerRepository(), new SimpleInterceptorChainFactory(interceptors), Collections
        .singletonList(new SimpleInvokerFactory()), new DefaultUnitOfWorkFactory());
  }

  private SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> interceptors, UnitOfWorkFactory unitOfWorkFactory) {
    return new SynchronousEventBus(new InMemoryInvokerRepository(), new SimpleInterceptorChainFactory(interceptors), Collections
        .singletonList(new SimpleInvokerFactory()), unitOfWorkFactory);
  }

  private SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> interceptors, List<InvokerFactory> invokerFactories) {
    return new SynchronousEventBus(new InMemoryInvokerRepository(), new SimpleInterceptorChainFactory(interceptors), invokerFactories,
        new DefaultUnitOfWorkFactory());
  }

}
