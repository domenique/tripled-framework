package eu.tripled.command;

import java.lang.annotation.*;

/**
 * Annotation to indicate that the annotated type is a CommandHandler.
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventHandler {

}
