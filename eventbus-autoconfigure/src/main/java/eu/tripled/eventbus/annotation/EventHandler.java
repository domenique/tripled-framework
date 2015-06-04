package eu.tripled.eventbus.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Annotation to indicate that the annotated type is an EventHandler.
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface EventHandler {

}
