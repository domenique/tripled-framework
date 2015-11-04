package eu.tripledframework.eventbus.domain.invoker;

/**
 * An EventHandlerInvoker is responsible for invoking a method which handles an event.
 * <p>
 * This method can be on a simple object, or if the implementation supports it, it could be made in such a way that
 * it instantiates the object before calling the method.
 */
public interface EventHandlerInvoker {

  /**
   * Method to verify if this EventHandlerInvoker is able to invoke an event of the given type.
   *
   * @param eventTypeToHandle The type of event that needs to be checked.
   * @return <code>true</code> if the EventHandlerInvoker is able to handle events of the given type,
   * <code>false</code> otherwise.
   */
  boolean handles(Class<?> eventTypeToHandle);

  /**
   * returns the type of event it is able to handle
   *
   * @return The class of the event it supports.
   */
  @Deprecated
  Class<?> getEventType();

  /**
   * Method which checks if the method which is being invoked by this invoker has a return type.
   *
   * @return <code>true</code> if the method which will be invoked has a return type, <code>false</code> otherwise.
   */
  boolean hasReturnType();

  /**
   * Invokes the method handler with the given object as input.
   *
   * @param object The event with which the method handler should be invoked.
   * @return an optional response from the method handler.
   */
  Object invoke(Object object);
}
