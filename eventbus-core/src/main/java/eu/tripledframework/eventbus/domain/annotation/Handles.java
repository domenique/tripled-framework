package eu.tripledframework.eventbus.domain.annotation;

import java.lang.annotation.*;

/**
 * Method to indicate that the annotated method is capable of handling an Event.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Handles {

  Class<?> value();

}