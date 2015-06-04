package eu.tripled.eventbus.asynchronous;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import eu.tripled.eventbus.*;
import eu.tripled.eventbus.callback.CommandValidationException;
import eu.tripled.eventbus.callback.FutureEventCallback;
import eu.tripled.eventbus.interceptor.TestValidator;
import eu.tripled.eventbus.interceptor.ValidatingEventBusInterceptor;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class AsynchronousEventBusTest {

  public static final String THREAD_POOL_PREFIX = "CDForTest-pool-";
  public static final String THREAD_POOL_WITH_VALIDATION_PREFIX = "CDForTest-Val-pool-";
  private EventPublisher asynchronousDispatcher;
  private EventPublisher dispatcherWithValidation;
  private TestEventHandler eventHandler;
  private TestValidator validator;

  @Before
  public void setUp() throws Exception {
    eventHandler = new TestEventHandler();

    ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
        .setNameFormat(THREAD_POOL_PREFIX + "%d")
        .build());
    AsynchronousEventBus eventBus = new AsynchronousEventBus(executor);
    eventBus.subscribe(eventHandler);
    asynchronousDispatcher = eventBus;

    ExecutorService executor2 = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
        .setNameFormat(THREAD_POOL_WITH_VALIDATION_PREFIX + "%d")
        .build());
    validator = new TestValidator();
    List<EventBusInterceptor> interceptors = Lists.newArrayList(new ValidatingEventBusInterceptor(validator));
    AsynchronousEventBus eventBus2 = new AsynchronousEventBus(interceptors, executor2);
    eventBus2.subscribe(eventHandler);
    dispatcherWithValidation = eventBus2;

  }

  @Test
  public void whenNotGivenAnExecutor_shouldCreateADefaultOneAndExecuteCommands() throws Exception {
    // given
    AsynchronousEventBus defaultPublisher = new AsynchronousEventBus();
    TestEventHandler eventHandler = new TestEventHandler();
    defaultPublisher.subscribe(eventHandler);
    FutureEventCallback<Void> future = new FutureEventCallback<>();
    HelloCommand command = new HelloCommand("domenique");

    // when
    defaultPublisher.publish(command, future);
    future.get();

    // then
    assertThat(future.isDone()).isEqualTo(true);
    assertThat(eventHandler.isHelloCommandHandled).isTrue();
  }

  @Test
  public void whenGivenAValidCommand_shouldBeExecutedAsynchronous() throws Exception {
    // given
    HelloCommand command = new HelloCommand("Domenique");
    FutureEventCallback<Void> future = new FutureEventCallback<>();

    // when
    asynchronousDispatcher.publish(command, future);
    future.get();

    // then
    assertThat(eventHandler.isHelloCommandHandled).isEqualTo(true);
    assertThat(eventHandler.threadNameForExecute).isEqualTo(THREAD_POOL_PREFIX + "0");
  }

  @Test
  public void whenGivenAValidCommandAndFutureCallback_waitForExecutionToFinish() throws Exception {
    // given
    HelloCommand command = new HelloCommand("Domenique");
    FutureEventCallback<Void> future = new FutureEventCallback<>();

    // when
    asynchronousDispatcher.publish(command, future);
    future.get();

    // then
    assertThat(future.isDone()).isEqualTo(true);
    assertThat(eventHandler.isHelloCommandHandled).isEqualTo(true);
    assertThat(eventHandler.threadNameForExecute).isEqualTo(THREAD_POOL_PREFIX + "0");
  }

  @Test
  public void whenUsingADispatcherWithValidator_validationShouldBeDoneAsynchronous() throws Exception {
    // given
    FutureEventCallback<Void> future = new FutureEventCallback<>();
    ValidatingCommand command = new ValidatingCommand("should pass");
    validator.shouldFailNextCall(false);

    // when
    dispatcherWithValidation.publish(command, future);
    future.get();

    // then
    assertThat(future.isDone()).isEqualTo(true);
    assertThat(eventHandler.isValidatingCommandHandled).isEqualTo(true);
    assertThat(eventHandler.threadNameForExecute).isEqualTo(THREAD_POOL_WITH_VALIDATION_PREFIX + "0");
  }

  @Test
  public void whenGivenAnInvalidCommand_shouldFail() throws Exception {
    // given
    FutureEventCallback<Void> future = new FutureEventCallback<>();
    ValidatingCommand command = new ValidatingCommand(null);
    validator.shouldFailNextCall(true);

    // when
    dispatcherWithValidation.publish(command, future);

    try {
      future.get();
    } catch(ExecutionException ex) {
      // then
      assertThat(future.isDone()).isEqualTo(true);
      assertThat(ex).hasCauseInstanceOf(CommandValidationException.class);
      assertThat(validator.isValidateCalled).isEqualTo(true);
      assertThat(eventHandler.isValidatingCommandHandled).isEqualTo(false);
    }
  }

  @Test
  public void whenGivenACommandWhichFails_shouldFail() throws Exception {
    // given
    FutureEventCallback<Void> future = new FutureEventCallback<>();
    FailingCommand command = new FailingCommand();

    // when
    dispatcherWithValidation.publish(command, future);

    try {
      future.get();
    } catch (ExecutionException ex) {
      assertThat(future.isDone()).isEqualTo(true);
      assertThat(ex).hasRootCauseInstanceOf(IllegalStateException.class);
      assertThat(eventHandler.isFailingCommandHandled).isEqualTo(true);
    }


  }
}