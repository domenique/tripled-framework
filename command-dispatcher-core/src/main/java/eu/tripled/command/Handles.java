package eu.tripled.command;

import java.lang.annotation.*;

/**
 * Method to indicate that the annotated method is capable of handling a a CommandMessage.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Handles {

  Class<?> value();
}
