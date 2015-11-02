package eu.tripledframework.eventbus.domain.synchronous;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import eu.tripledframework.eventbus.command.ACommandHandledByMultipleHandlers;
import eu.tripledframework.eventbus.command.FailingCommand;
import eu.tripledframework.eventbus.command.HelloCommand;
import eu.tripledframework.eventbus.command.UnhandledCommand;
import eu.tripledframework.eventbus.command.ValidatingCommand;
import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.EventCallback;
import eu.tripledframework.eventbus.domain.EventPublisher;
import eu.tripledframework.eventbus.domain.EventSubscriber;
import eu.tripledframework.eventbus.domain.callback.ExceptionThrowingEventCallback;
import eu.tripledframework.eventbus.domain.callback.FutureEventCallback;
import eu.tripledframework.eventbus.domain.dispatcher.EventHandlerNotFoundException;
import eu.tripledframework.eventbus.domain.interceptor.CommandValidationException;
import eu.tripledframework.eventbus.domain.interceptor.LoggingEventBusInterceptor;
import eu.tripledframework.eventbus.domain.interceptor.TestValidator;
import eu.tripledframework.eventbus.domain.interceptor.ValidatingEventBusInterceptor;
import eu.tripledframework.eventbus.domain.invoker.DuplicateEventHandlerRegistrationException;
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

  @Test(expected = IllegalStateException.class)
  public void whenGivenACommandThatFails_exceptionShouldBeThrown() throws Exception {
    // given
    FailingCommand command = new FailingCommand();

    // when
    eventPublisher.publish(command);

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
      public void onFailure(Throwable exception) {
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

    expectedException.expect(instanceOf(CommandValidationException.class));

    // when
    eventPublisher.publish(validatingCommand);

    // then --> exception
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
      public void onFailure(Throwable exception) {
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