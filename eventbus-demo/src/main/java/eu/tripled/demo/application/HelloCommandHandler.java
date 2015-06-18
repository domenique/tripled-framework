package eu.tripled.demo.application;

import eu.tripled.demo.SaidHelloDomainEvent;
import eu.tripled.eventbus.EventPublisher;
import eu.tripled.eventbus.annotation.EventHandler;
import eu.tripled.eventbus.annotation.Handles;
import org.springframework.beans.factory.annotation.Autowired;

@EventHandler
public class HelloCommandHandler {

  @Autowired
  private EventPublisher eventPublisher;

  @Handles(HelloCommand.class)
  public HelloResponse handleHelloCommand(HelloCommand helloCommand) {
    HelloResponse helloResponse = new HelloResponse("Hello " + helloCommand.getName());

    eventPublisher.publish(new SaidHelloDomainEvent(helloCommand.getName()));

    return helloResponse;
  }

}
