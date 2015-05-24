package eu.tripled.command.dispatcher;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import eu.tripled.command.CommandDispatcher;
import eu.tripled.command.CommandDispatcherInterceptor;
import eu.tripled.command.callback.CommandFailedException;
import eu.tripled.command.callback.CommandValidationException;
import eu.tripled.command.callback.ExceptionThrowingCommandCallback;
import eu.tripled.command.MyCommand;
import eu.tripled.command.MyCommandWhichFailsWithException;
import eu.tripled.command.MyCommandWithReturnType;
import eu.tripled.command.MyCommandWithoutValidation;
import eu.tripled.command.interceptor.LoggingCommandDispatcherInterceptor;
import eu.tripled.command.interceptor.TestCommandDispatcherInterceptor;
import eu.tripled.command.interceptor.ValidatingCommandDispatcherInterceptor;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SynchronousCommandDispatcherTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private CommandDispatcher dispatcherWithLoggingAndValidation;

  @Before
  public void setUp() throws Exception {
    List<CommandDispatcherInterceptor> interceptors = new ArrayList<>();
    interceptors.add(0, new LoggingCommandDispatcherInterceptor());
    interceptors.add(1, new ValidatingCommandDispatcherInterceptor());

    dispatcherWithLoggingAndValidation = new SynchronousCommandDispatcher(interceptors);
  }

  @Test
  public void whenGivenNothing_shouldThrowException() throws Exception {
    // given
    CommandDispatcher dispatcherWithoutInterceptors = new SynchronousCommandDispatcher();
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("The command cannot be null.");

    // when
    dispatcherWithoutInterceptors.dispatch(null);

    // then -> Exception
  }

  @Test
  public void whenGivenAValidCommand_shouldValidateAndExecute() throws Exception {
    // when
    MyCommand command = new MyCommand(true);

    // when
    dispatcherWithLoggingAndValidation.dispatch(command);

    // then
    assertThat(command.isValidateCalled).isEqualTo(true);
    assertThat(command.isExecuteCalled).isEqualTo(true);
  }

  @Test
  public void whenGivenAValidCommandThatShouldNotBeValidated_shouldExecuteCommand() throws Exception {
    // when
    MyCommandWithoutValidation command = new MyCommandWithoutValidation();

    // when
    dispatcherWithLoggingAndValidation.dispatch(command);

    // then
    assertThat(command.isExecuteCalled).isEqualTo(true);
  }

  @Test
  public void whenGivenAnInvalidCommand_shouldThrowException() throws Exception {
    // when
    MyCommand command = new MyCommand(false);

    // when
    Throwable exception = null;
    try {
      dispatcherWithLoggingAndValidation.dispatch(command);
    } catch (Exception e) {
      exception = e;
    }

    // then
    assertThat(command.isValidateCalled).isEqualTo(true);
    assertThat(exception).isInstanceOf(CommandValidationException.class);
    assertThat(command.isExecuteCalled).isEqualTo(false);
  }

  @Test
  public void whenGivenACommandWithResponse_shouldReturnResponse() throws Exception {
    // given
    String expectedResponse = "the response";
    MyCommandWithReturnType command = new MyCommandWithReturnType(expectedResponse);

    // when
    ExceptionThrowingCommandCallback<String> callback = new ExceptionThrowingCommandCallback<>();
    dispatcherWithLoggingAndValidation.dispatch(command, callback);

    // then
    assertThat(callback.getResult()).isEqualTo(expectedResponse);
    assertThat(command.isExecuteCalled).isEqualTo(true);
  }

  @Test
  public void whenGivenACommandWhichFails_shouldWrapException() throws Exception {
    // given
    MyCommandWhichFailsWithException command = new MyCommandWhichFailsWithException(true);

    // when
    try {
      dispatcherWithLoggingAndValidation.dispatch(command);
      fail("The command should not pass.");
    } catch (Throwable exception) {
      // then
      assertThat(exception)
          .isInstanceOf(CommandFailedException.class)
          .hasCauseInstanceOf(IllegalStateException.class);
    }
  }

  @Test
  public void whenGivenACommandAndInterceptor_interceptorShouldBeCalled() throws Exception {
    // given
    TestCommandDispatcherInterceptor interceptor = new TestCommandDispatcherInterceptor();
    SynchronousCommandDispatcher dispatcher = new SynchronousCommandDispatcher(Lists.newArrayList(interceptor));
    MyCommand command = new MyCommand(true);

    // when
    dispatcher.dispatch(command);

    // then
    assertThat(interceptor.isInterceptorCalled).isEqualTo(true);
    assertThat(command.isExecuteCalled).isEqualTo(true);
  }
}