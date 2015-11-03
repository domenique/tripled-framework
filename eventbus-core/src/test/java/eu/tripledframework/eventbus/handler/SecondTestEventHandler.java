package eu.tripledframework.eventbus.handler;

import eu.tripledframework.eventbus.command.HelloCommand;
import eu.tripledframework.eventbus.domain.annotation.EventHandler;
import eu.tripledframework.eventbus.domain.annotation.Handles;

@EventHandler
public class SecondTestEventHandler {

  public boolean isHelloCommandHandled;

  @Handles(HelloCommand.class)
  public String stringReturning(HelloCommand command) {
    isHelloCommandHandled = true;
    return "Hello " + command.getName();
  }

}
