package eu.tripled.demo.application;

import eu.tripled.eventbus.annotation.EventHandler;
import eu.tripled.eventbus.annotation.Handles;

@EventHandler
public class HelloCommandHandler {

  @Handles(HelloCommand.class)
  public HelloResponse handleHelloCommand(HelloCommand helloCommand) {
    return new HelloResponse("Hello " + helloCommand.getName());
  }

}
