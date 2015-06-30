package eu.tripledframework.eventstore.domain.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map a method parameter to an event property. This is used in conjunction with the
 * {@link ConstructionHandler} to map event properties to method parameters.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface EP {

  /**
   * The name of the event property to map to.
   *
   * @return The name of the event property
   */
  String value();
}
