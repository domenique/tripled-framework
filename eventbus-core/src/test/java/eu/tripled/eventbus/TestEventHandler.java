package eu.tripled.eventbus;

import eu.tripled.eventbus.annotation.Handles;

public class TestEventHandler {

  public boolean isHelloCommandHandled;
  public boolean isFailingCommandHandled;
  public boolean isValidatingCommandHandled;
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
  public void handleFailingCommand(FailingCommand failingCommand) {
    isFailingCommandHandled = true;
    throw new IllegalStateException("could not execute command.");
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
