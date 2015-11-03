package eu.tripledframework.eventbus.domain.annotation;


import java.lang.annotation.*;

/**
 * Annotation to indicate that the annotated type is an EventHandler.
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
// @Component
public @interface EventHandler {

}
