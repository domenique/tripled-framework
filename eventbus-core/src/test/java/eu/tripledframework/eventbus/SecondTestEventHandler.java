package eu.tripledframework.eventbus;

import eu.tripledframework.eventbus.annotation.Handles;

public class SecondTestEventHandler {

  public boolean isHelloCommandHandled;

  @Handles(HelloCommand.class)
  public String stringReturning(HelloCommand command) {
    isHelloCommandHandled = true;
    return "Hello " + command.getName();
  }

}
