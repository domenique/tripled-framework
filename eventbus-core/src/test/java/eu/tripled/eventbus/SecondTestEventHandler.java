package eu.tripled.eventbus;

import eu.tripled.eventbus.annotation.Handles;

public class SecondTestEventHandler {

  public boolean isHelloCommandHandled;

  @Handles(HelloCommand.class)
  public String stringReturning(HelloCommand command) {
    isHelloCommandHandled = true;
    return "Hello " + command.getName();
  }

}
