package eu.tripledframework.eventbus.domain.invoker;

import java.util.List;

/**
 * A Factory to create EventHandlerInvoker objects.
 * <p>
 * Override this class if you want the eventbus to create alternative EventHandlerInvoker objects.
 * <p>
 * When subscribing an object to the eventbus, the appropriate factory will be used depending on the result of the
 * supports methods. The Factories will be queried in the order specified in which they are given to the eventBus
 * when it is configured.
 */
public interface EventHandlerInvokerFactory {

  /**
   * Method which will create one or more EventHandlerInvoker objects for the given object.
   *
   * @param eventHandler The object for which EventHandlerInvoker objects should be created.
   * @return A List of EventHandlerInvoker objects, or an empty list. never null.
   */
  List<EventHandlerInvoker> create(Object eventHandler);

  /**
   * Returns true if this factory is capable of creating EventHandlerInvoker objects for the given object,
   * false otherwise.
   *
   * This method is called by the EventBus prior to calling the create.
   * @param object The object for which the test should be done.
   * @return <code>true</code> if this factory supports the given class, <code>false</code> otherwise.
   */
  boolean supports(Object object);
}
