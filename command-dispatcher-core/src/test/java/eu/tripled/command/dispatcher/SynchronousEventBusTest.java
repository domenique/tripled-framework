package eu.tripled.command.dispatcher;

import eu.tripled.command.*;
import eu.tripled.command.callback.CommandFailedException;
import eu.tripled.command.callback.CommandValidationException;
import eu.tripled.command.callback.ExceptionThrowingEventCallback;
import eu.tripled.command.interceptor.LoggingEventBusInterceptor;
import eu.tripled.command.interceptor.ValidatingEventBusInterceptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class SynchronousEventBusTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private Publisher publisher;
  private TestEventHandler eventHandler;

  @Before
  public void setUp() throws Exception {
    List<EventBusInterceptor> interceptors = new ArrayList<>();
    interceptors.add(0, new LoggingEventBusInterceptor());
    interceptors.add(1, new ValidatingEventBusInterceptor());

    SynchronousEventBus eventBus = new SynchronousEventBus(interceptors);

    eventHandler = new TestEventHandler();
    eventBus.subscribe(eventHandler);

    publisher = eventBus;
  }

  @Test
  public void whenGivenAHelloCommand_shouldCallEventHandler() throws Exception {
    // given
    HelloCommand helloCommand = new HelloCommand("Domenique");

    // given
    publisher.publish(helloCommand);

    // then
    assertThat(eventHandler.isHelloCommandHandled).isTrue();
  }

  @Test
  public void whenGivenAHelloCommandAndCallback_shouldBeAbleToRetrieveResponse() throws Exception {
    // given
    HelloCommand helloCommand = new HelloCommand("Domenique");
    ExceptionThrowingEventCallback<String> callback = new ExceptionThrowingEventCallback<>();

    // given
    publisher.publish(helloCommand, callback);

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
    publisher.publish(command);

    // then --> exception
  }

  @Test
  public void whenGivenCommandThatFailsValidation_shouldInvokeCallback() throws Exception {
    // given
    ValidatingCommand validatingCommand = new ValidatingCommand(false);

    // when
    publisher.publish(validatingCommand, new EventCallback<Void>() {
      @Override
      public void onSuccess(Void result) {
        fail("onSuccess should not be called.");
      }

      @Override
      public void onValidationFailure(Command command) {
        // then
        assertThat(command.getBody()).isSameAs(validatingCommand);
      }

      @Override
      public void onFailure(Throwable exception) {
        fail("onFailure should not be called.");
      }
    });

    assertThat(eventHandler.isValidatingCommandHandled).isFalse();
  }

  @Test(expected = CommandValidationException.class)
  public void whenGivenCommandThatFailsValidation_shouldThrowException() throws Exception {
    // given
    ValidatingCommand validatingCommand = new ValidatingCommand(false);

    // when
    publisher.publish(validatingCommand);

    // then --> exception
  }

  @Test
  public void whenGivenCommandThatSucceeds_shouldInvokeCallback() throws Exception {
    // given
    ValidatingCommand validatingCommand = new ValidatingCommand(true);

    // when
    publisher.publish(validatingCommand, new EventCallback<Void>() {
      @Override
      public void onSuccess(Void result) {
        assertThat(result).isNull();
      }

      @Override
      public void onValidationFailure(Command command) {
        fail("onValidationFailure should not be called.");

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
    publisher.publish(command);

    // then --> exception
  }

  @Test
  public void whenRegisteringDuplicateEventHandler_alreadyRegisteredHandlerShouldNotBeInvoked() throws Exception {
    // given
    SecondTestEventHandler secondEventHandler = new SecondTestEventHandler();
    ((Subscriber) publisher).subscribe(secondEventHandler);
    HelloCommand command = new HelloCommand("domenique");

    // when
    publisher.publish(command);

    // then
    assertThat(eventHandler.isHelloCommandHandled).isFalse();
    assertThat(secondEventHandler.isHelloCommandHandled).isTrue();

  }
}