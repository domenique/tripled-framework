package eu.tripledframework.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.tripledframework.demo.model.Address;
import eu.tripledframework.eventstore.domain.EventStore;
import eu.tripledframework.eventstore.infrastructure.InMemoryEventRepository;

@Configuration
public class EventStoreConfiguration {

  @Bean
  public EventStore<Address> addressEventStore() {
    return new EventStore<>(Address.class, new InMemoryEventRepository());
  }
}
