package eu.tripledframework.demo.infrastructure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.tripledframework.demo.SaidHelloDomainEvent;
import eu.tripledframework.eventbus.domain.annotation.EventHandler;
import eu.tripledframework.eventbus.domain.annotation.Handles;

@EventHandler
@Component
public class InMemoryHelloEventStore {

  private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryHelloEventStore.class);

  private List<Object> events;

  public InMemoryHelloEventStore() {
    this.events = new ArrayList<>();
  }

  @Handles(SaidHelloDomainEvent.class)
  public void handleSaidHelloDomainEvent(SaidHelloDomainEvent event) {
    LOGGER.info("Received SaidHelloDomainEvent.");
    this.events.add(event);
  }
}
