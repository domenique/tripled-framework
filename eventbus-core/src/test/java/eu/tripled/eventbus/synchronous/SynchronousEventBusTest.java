package eu.tripled.eventbus.synchronous;

import eu.tripled.eventbus.*;
import eu.tripled.eventbus.callback.CommandFailedException;
import eu.tripled.eventbus.interceptor.CommandValidationException;
import eu.tripled.eventbus.callback.ExceptionThrowingEventCallback;
import eu.tripled.eventbus.callback.FutureEventCallback;
import eu.tripled.eventbus.dispatcher.EventHandlerNotFoundException;
import eu.tripled.eventbus.invoker.DuplicateEventHandlerRegistrationException;
import eu.tripled.eventbus.interceptor.LoggingEventBusInterceptor;
import eu.tripled.eventbus.interceptor.TestValidator;
import eu.tripled.eventbus.interceptor.ValidatingEventBusInterceptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.CoreMatchers.instanceOf;


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
    assertThat(eventHandler.isHelloCommandHandled).isTrue();
  }

  @Test
  public void whenGivenAHelloCommandAndCallback_shouldBeAbleToRetrieveResponse() throws Exception {
    // given
    HelloCommand helloCommand = new HelloCommand("Domenique");
    ExceptionThrowingEventCallback<String> callback = new ExceptionThrowingEventCallback<>();

    // given
    eventPublisher.publish(helloCommand, callback);

    // then
    assertThat(callback.getResult()).isEqualTo("Hello Domenique");
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
    assertThat(myEventHandler.isHelloCommandHandled).isTrue();
  }

  @Test(expected = CommandFailedException.class)
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

    assertThat(eventHandler.isValidatingCommandHandled).isFalse();
  }

  @Test
  public void whenGivenCommandThatFailsValidation_shouldThrowException() throws Exception {
    // given
    ValidatingCommand validatingCommand = new ValidatingCommand(null);
    validator.shouldFailNextCall(true);

    expectedException.expectCause(instanceOf(CommandValidationException.class));

    // when
    eventPublisher.publish(validatingCommand);

    // then --> exception
  }

  @Test
  public void whenGivenACommandWhichFails_shouldFail() throws Exception {
    // given
    Future<Void> future = new FutureEventCallback<>();
    FailingCommand command = new FailingCommand();

    // when
    eventPublisher.publish(command, future);

    try {
      future.get();
    } catch (ExecutionException ex) {
      assertThat(future.isDone()).isEqualTo(true);
      assertThat(ex).hasRootCauseInstanceOf(IllegalStateException.class);
      assertThat(eventHandler.isFailingCommandHandled).isEqualTo(true);
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
        assertThat(result).isNull();
      }

      @Override
      public void onFailure(Throwable exception) {
        fail("onFailure should not be called.");
      }
    });

    assertThat(eventHandler.isValidatingCommandHandled).isTrue();
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
    assertThat(eventHandler.handledByFirstCount).isEqualTo(1);
    assertThat(eventHandler.handledBySecondCount).isEqualTo(1);
  }

  @Test
  public void whenRegisteringADuplicateEventHandler_shouldNotInvokeTwice() throws Exception {
    // given
    ACommandHandledByMultipleHandlers command = new ACommandHandledByMultipleHandlers();
    ((EventSubscriber) eventPublisher).subscribe(eventHandler);

    // when
    eventPublisher.publish(command);

    // then
    assertThat(eventHandler.handledByFirstCount).isEqualTo(1);
    assertThat(eventHandler.handledBySecondCount).isEqualTo(1);


  }
}