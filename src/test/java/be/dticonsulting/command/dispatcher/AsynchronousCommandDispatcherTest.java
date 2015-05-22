package be.dticonsulting.command.dispatcher;

import be.dticonsulting.command.CommandDispatcher;
import be.dticonsulting.command.callback.FutureCommandCallback;
import be.dticonsulting.command.command.MyCommand;
import be.dticonsulting.command.interceptor.ValidatingCommandDispatcherInterceptor;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class AsynchronousCommandDispatcherTest {

  public static final String THREAD_POOL_PREFIX = "CDForTest-pool-";
  private CommandDispatcher asynchronousDispatcher;

  @Before
  public void setUp() throws Exception {
    ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
        .setNameFormat(THREAD_POOL_PREFIX + "%d")
        .build());
    asynchronousDispatcher = new AsynchronousCommandDispatcher(executor);
  }

  @Test(timeout = 100)
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

  @Test(timeout = 100)
  public void whenGivenAValidCommand_shouldBeExecutedAsynchronous() throws Exception {
    // given
    MyCommand command = new MyCommand(true);

    // when
    asynchronousDispatcher.dispatch(command);

    // then
    Thread.sleep(10);
    assertThat(command.isValidateCalled).isEqualTo(false);
    assertThat(command.isExecuteCalled).isEqualTo(true);
    assertThat(command.threadNameForExecute).isEqualTo(THREAD_POOL_PREFIX + "0");
  }

  @Test(timeout = 100)
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

  @Test
  public void whenUsingADispatcherWithValidator_validationShouldBeDoneAsynchronous() throws Exception {
    // given
    ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
        .setNameFormat(THREAD_POOL_PREFIX + "%d")
        .build());
    CommandDispatcher dispatcherWithValidation = new AsynchronousCommandDispatcher(Lists.newArrayList(new ValidatingCommandDispatcherInterceptor()), executor);
    FutureCommandCallback<Void> future = new FutureCommandCallback<>();
    MyCommand command = new MyCommand(true);

    // when
    dispatcherWithValidation.dispatch(command, future);
    future.get();

    // then
    assertThat(future.isDone()).isEqualTo(true);
    assertThat(command.isValidateCalled).isEqualTo(true);
    assertThat(command.isExecuteCalled).isEqualTo(true);
    assertThat(command.threadNameForExecute).isEqualTo(THREAD_POOL_PREFIX + "0");
    assertThat(command.threadNameForValidate).isEqualTo(THREAD_POOL_PREFIX + "0");

  }
}