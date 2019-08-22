package eu.tripledframework.eventbus.internal.domain;

import eu.tripledframework.eventbus.CommandCallback;
import eu.tripledframework.eventbus.CommandDispatcher;
import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.command.ValidatingCommand;
import eu.tripledframework.eventbus.handler.TestCommandHandler;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.CommandValidationException;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.LoggingEventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.TestValidator;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.ValidatingEventBusInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class CommandDispatchingWithValidationTests extends AbstractEventBusTest {

  private CommandDispatcher commandDispatcher;
  private TestCommandHandler eventHandler;
  private TestValidator validator;

  @BeforeEach
  void setUp() throws Exception {
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
  void whenGivenCommandThatFailsValidation_shouldInvokeCallback() throws Exception {
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
  void whenGivenCommandThatFailsValidation_shouldThrowException() throws Exception {
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
