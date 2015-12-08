/*
 * Copyright 2015 TripleD framework.
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
package eu.tripledframework.eventbus.handler;

import eu.tripledframework.eventbus.command.ACommandHandledByMultipleHandlers;
import eu.tripledframework.eventbus.command.CommandHandledByAPrivateMethod;
import eu.tripledframework.eventbus.command.FailingCommand;
import eu.tripledframework.eventbus.command.FailingCommandWithCheckedException;
import eu.tripledframework.eventbus.command.HelloCommand;
import eu.tripledframework.eventbus.command.ValidatingCommand;
import eu.tripledframework.eventbus.domain.annotation.EventHandler;
import eu.tripledframework.eventbus.domain.annotation.Handles;

@EventHandler
public class TestEventHandler {

  public boolean isHelloCommandHandled;
  public boolean isFailingCommandHandled;
  public boolean isValidatingCommandHandled;
  public boolean isCommandHandledByAPrivateMethodCalled;
  public int handledByFirstCount;
  public int handledBySecondCount;
  public String threadNameForExecute;

  @Handles(HelloCommand.class)
  public String stringReturning(HelloCommand command) {
    isHelloCommandHandled = true;
    threadNameForExecute = Thread.currentThread().getName();
    return "Hello " + command.getName();
  }

  @Handles(FailingCommand.class)
  public String handleFailingCommand(FailingCommand failingCommand) {
    isFailingCommandHandled = true;
    throw new IllegalStateException("could not execute command.");
  }

  @Handles(CommandHandledByAPrivateMethod.class)
  private void handleCommandHandledByAPrivateMethod(CommandHandledByAPrivateMethod privateCommand) {
    isCommandHandledByAPrivateMethodCalled = true;
  }

  @Handles(FailingCommandWithCheckedException.class)
  public String handleFailingCommandWithCheckedException(FailingCommandWithCheckedException failingCommand) throws Exception {
    isFailingCommandHandled = true;
    throw new Exception("could not execute command.");
  }

  @Handles(ValidatingCommand.class)
  public void handleValidatingCommand(ValidatingCommand command) {
    threadNameForExecute = Thread.currentThread().getName();
    isValidatingCommandHandled = true;
  }

  @Handles(ACommandHandledByMultipleHandlers.class)
  public void handleFirst(ACommandHandledByMultipleHandlers command) {
    handledByFirstCount++;
  }

  @Handles(ACommandHandledByMultipleHandlers.class)
  public String handleSecond(ACommandHandledByMultipleHandlers command) {
    handledBySecondCount++;
    return "Handled by second.";
  }

}
