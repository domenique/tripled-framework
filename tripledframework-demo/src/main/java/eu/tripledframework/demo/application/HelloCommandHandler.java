package eu.tripledframework.demo.application;

import eu.tripledframework.demo.SaidHelloDomainEvent;
import eu.tripledframework.eventbus.domain.EventPublisher;
import eu.tripledframework.eventbus.domain.annotation.EventHandler;
import eu.tripledframework.eventbus.domain.annotation.Handles;
import org.springframework.beans.factory.annotation.Autowired;

@EventHandler
public class HelloCommandHandler {

  @Autowired
  private EventPublisher eventPublisher;

  @Handles(HelloCommand.class)
  public HelloResponse handleHelloCommand(HelloCommand helloCommand) {
    if (helloCommand.getName().equals("The devil")) {
      throw new IllegalArgumentException("I'm not saying hi to the devil! :P");
    }
    HelloResponse helloResponse = new HelloResponse("Hello " + helloCommand.getName());

    eventPublisher.publish(new SaidHelloDomainEvent(helloCommand.getName()));

    return helloResponse;
  }

}
