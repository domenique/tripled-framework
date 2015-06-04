package eu.tripled.eventbus;

import eu.tripled.eventbus.annotation.Handles;

public class TestEventHandler {

  public boolean isHelloCommandHandled;
  public boolean isFailingCommandHandled;
  public boolean isValidatingCommandHandled;
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

}
