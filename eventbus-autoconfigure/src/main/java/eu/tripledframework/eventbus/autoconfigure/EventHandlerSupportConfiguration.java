package eu.tripledframework.eventbus.autoconfigure;

import eu.tripledframework.eventbus.domain.EventSubscriber;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventHandlerSupportConfiguration {

  @Bean
  public BeanPostProcessor eventHandlerSupportBeanPostProcessor(EventSubscriber eventSubscriber) {
    return new EventHandlerRegistrationBeanPostProcessor(eventSubscriber);
  }
}
