package be.dticonsulting.support.command.application;

import be.dticonsulting.support.command.application.callback.CommandFailedException;
import be.dticonsulting.support.command.application.callback.CommandValidationException;
import be.dticonsulting.support.command.application.callback.ExceptionThrowingCommandCallback;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SynchronousCommandDispatcherTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private SynchronousCommandDispatcher synchronousCommandDispatcher;

  @Before
  public void setUp() throws Exception {
    synchronousCommandDispatcher = new SynchronousCommandDispatcher();
  }

  @Test
  public void whenGivenNothing_shouldThrowException() throws Exception {
    // given
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("The command cannot be null.");

    // when
    synchronousCommandDispatcher.dispatch(null);

    // then -> Exception
  }

  @Test
  public void whenGivenAValidCommand_shouldValidateAndExecute() throws Exception {
    // when
    MyCommand command = new MyCommand(true);

    // when
    synchronousCommandDispatcher.dispatch(command);

    // then
    assertThat(command.isValidateCalled).isEqualTo(true);
    assertThat(command.isExecuteCalled).isEqualTo(true);
  }

  @Test
  public void whenGivenAValidCommandThatShouldNotBeValidated_shouldExecuteCommand() throws Exception {
    // when
    MyCommandWithoutValidation command = new MyCommandWithoutValidation();

    // when
    synchronousCommandDispatcher.dispatch(command);

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
      synchronousCommandDispatcher.dispatch(command);
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
    synchronousCommandDispatcher.dispatch(command, callback);

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
      synchronousCommandDispatcher.dispatch(command);
      fail("The command should not pass.");
    } catch (Throwable exception) {
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
    assertThat(command.isValidateCalled).isEqualTo(true);

  }
}