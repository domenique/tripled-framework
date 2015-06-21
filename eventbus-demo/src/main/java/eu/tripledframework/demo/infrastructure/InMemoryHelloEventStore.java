package eu.tripledframework.demo.infrastructure;

import eu.tripledframework.demo.SaidHelloDomainEvent;
import eu.tripledframework.eventbus.annotation.EventHandler;
import eu.tripledframework.eventbus.annotation.Handles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventHandler
public class InMemoryHelloEventStore {

  private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryHelloEventStore.class);

  @Handles(SaidHelloDomainEvent.class)
  public void handleSaidHelloDomainEvent(SaidHelloDomainEvent event) {
    LOGGER.info("Received SaidHelloDomainEvent.");
  }
}
