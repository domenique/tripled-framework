package eu.tripledframework.eventbus.internal.domain;

import eu.tripledframework.eventbus.CommandCallback;
import eu.tripledframework.eventbus.CommandDispatcher;
import eu.tripledframework.eventbus.EventBusInterceptor;
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
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.SimpleInterceptorChainFactory;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.DuplicateInvokerFoundException;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InMemoryInvokerRepository;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InvocationException;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InvokerNotFoundException;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.SimpleInvokerFactory;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.DefaultUnitOfWorkFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class CommandDispatchingTests extends AbstractEventBusTest {
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
