/*
 * Copyright 2022 TripleD framework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    var eventBus = createSynchronousEventBus(interceptors);

    eventHandler = new TestCommandHandler();
    eventBus.subscribe(eventHandler);

    commandDispatcher = eventBus;
  }

  @Test
  void whenGivenCommandThatFailsValidation_shouldInvokeCallback() throws Exception {
    // given
    var validatingCommand = new ValidatingCommand(null);
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
    var validatingCommand = new ValidatingCommand(null);
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
