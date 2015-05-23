package be.dticonsulting.command.dispatcher;

import be.dticonsulting.command.CommandDispatcher;
import be.dticonsulting.command.CommandDispatcherInterceptor;
import be.dticonsulting.command.callback.CommandValidationException;
import be.dticonsulting.command.callback.FutureCommandCallback;
import be.dticonsulting.command.command.MyCommand;
import be.dticonsulting.command.command.MyCommandWhichFailsWithException;
import be.dticonsulting.command.interceptor.ValidatingCommandDispatcherInterceptor;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class AsynchronousCommandDispatcherTest {

  public static final String THREAD_POOL_PREFIX = "CDForTest-pool-";
  public static final String THREAD_POOL_WITH_VALIDATION_PREFIX = "CDForTest-Val-pool-";
  private CommandDispatcher asynchronousDispatcher;
  private CommandDispatcher dispatcherWithValidation;

  @Before
  public void setUp() throws Exception {
    ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
        .setNameFormat(THREAD_POOL_PREFIX + "%d")
        .build());
    asynchronousDispatcher = new AsynchronousCommandDispatcher(executor);

    ExecutorService executor2 = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
        .setNameFormat(THREAD_POOL_WITH_VALIDATION_PREFIX + "%d")
        .build());
    List<CommandDispatcherInterceptor> interceptors = Lists.newArrayList(new ValidatingCommandDispatcherInterceptor());
    dispatcherWithValidation = new AsynchronousCommandDispatcher(interceptors, executor2);

  }

  @Test(timeout = 200)
  public void whenNotGivenAnExecutor_shouldCreateADefaultOneAndExecuteCommands() throws Exception {
    // given
    CommandDispatcher defaultCommandDispatcher = new AsynchronousCommandDispatcher();
    FutureCommandCallback<Void> future = new FutureCommandCallback<>();
    MyCommand command = new MyCommand(true);

    // when
    defaultCommandDispatcher.dispatch(command, future);
    future.get();

    // then
    assertThat(future.isDone()).isEqualTo(true);
    assertThat(command.isValidateCalled).isEqualTo(false);
    assertThat(command.isExecuteCalled).isEqualTo(true);
  }

  @Test(timeout = 200)
  public void whenGivenAValidCommand_shouldBeExecutedAsynchronous() throws Exception {
    // given
    MyCommand command = new MyCommand(true);
    FutureCommandCallback<Void> future = new FutureCommandCallback<>();

    // when
    asynchronousDispatcher.dispatch(command,future);
    future.get();

    // then
    assertThat(command.isValidateCalled).isEqualTo(false);
    assertThat(command.isExecuteCalled).isEqualTo(true);
    assertThat(command.threadNameForExecute).isEqualTo(THREAD_POOL_PREFIX + "0");
  }

  @Test(timeout = 200)
  public void whenGivenAValidCommandAndFutureCallback_waitForExecutionToFinish() throws Exception {
    // given
    MyCommand command = new MyCommand(true);
    FutureCommandCallback<Void> future = new FutureCommandCallback<>();

    // when
    asynchronousDispatcher.dispatch(command, future);
    future.get();

    // then
    assertThat(future.isDone()).isEqualTo(true);
    assertThat(command.isValidateCalled).isEqualTo(false);
    assertThat(command.isExecuteCalled).isEqualTo(true);
    assertThat(command.threadNameForExecute).isEqualTo(THREAD_POOL_PREFIX + "0");
  }

  @Test(timeout = 200)
  public void whenUsingADispatcherWithValidator_validationShouldBeDoneAsynchronous() throws Exception {
    // given
    FutureCommandCallback<Void> future = new FutureCommandCallback<>();
    MyCommand command = new MyCommand(true);

    // when
    dispatcherWithValidation.dispatch(command, future);
    future.get();

    // then
    assertThat(future.isDone()).isEqualTo(true);
    assertThat(command.isValidateCalled).isEqualTo(true);
    assertThat(command.isExecuteCalled).isEqualTo(true);
    assertThat(command.threadNameForExecute).isEqualTo(THREAD_POOL_WITH_VALIDATION_PREFIX + "0");
    assertThat(command.threadNameForValidate).isEqualTo(THREAD_POOL_WITH_VALIDATION_PREFIX + "0");
  }

  @Test
  public void whenGivenAnInvalidCommand_shouldFail() throws Exception {
    // given
    FutureCommandCallback<Void> future = new FutureCommandCallback<>();
    MyCommand command = new MyCommand(false);

    // when
    dispatcherWithValidation.dispatch(command, future);

    try {
      future.get();
    } catch(ExecutionException ex) {
      // then
      assertThat(future.isDone()).isEqualTo(true);
      assertThat(ex).hasCauseInstanceOf(CommandValidationException.class);
      assertThat(command.isValidateCalled).isEqualTo(true);
      assertThat(command.isExecuteCalled).isEqualTo(false);
    }
  }

  @Test
  public void whenGivenACommandWhichFails_shouldFail() throws Exception {
    // given
    FutureCommandCallback<Void> future = new FutureCommandCallback<>();
    MyCommandWhichFailsWithException command = new MyCommandWhichFailsWithException(true);

    // when
    dispatcherWithValidation.dispatch(command, future);

    try {
      future.get();
    } catch (ExecutionException ex) {
      assertThat(future.isDone()).isEqualTo(true);
      assertThat(ex).hasCauseInstanceOf(IllegalStateException.class);
      assertThat(command.isValidateCalled).isEqualTo(true);
      assertThat(command.isExecuteCalled).isEqualTo(true);
    }


  }
}