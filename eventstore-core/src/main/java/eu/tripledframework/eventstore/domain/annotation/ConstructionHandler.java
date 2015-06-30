package eu.tripledframework.eventstore.domain.annotation;

import eu.tripledframework.eventstore.domain.DomainEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method as being able to construct state using the given event.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface ConstructionHandler {

  /**
   * The event type that this method is able to handle.
   *
   * @return The DomainEvent type.
   */
  Class<? extends DomainEvent> value();
}
