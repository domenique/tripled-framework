package eu.tripled.demo.infrastructure;

import eu.tripled.demo.SaidHelloDomainEvent;
import eu.tripled.eventbus.annotation.EventHandler;
import eu.tripled.eventbus.annotation.Handles;
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
