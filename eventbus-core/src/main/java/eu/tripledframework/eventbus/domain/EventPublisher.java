package eu.tripledframework.eventbus.domain;

import java.util.concurrent.Future;

/**
 * The basic contract of the EventBus when publishing events.
 * <p>
 * An EventBusPublisher is responsible for dispatching events. The EventBus can, depending on the implementation
 * execute the event handling in a synchronous or asynchronous fashion.
 */
public interface EventPublisher {

  /**
   * Dispatches the given Event and invokes the given callback with either success or failure.
   *
   * @param event The event to publish.
   * @param callback The callback to invoke upon completion.
   * @param <ReturnType>   The return type of the event handling.
   */
  <ReturnType> void publish(Object event, EventCallback<ReturnType> callback);

  /**
   * Dispatches the given Event and invokes the given callback with either success or failure.
   *
   * @param event The event to publish.
   * @param <ReturnType>   The return type of the event handling.
   * @return A future object to be used to retrieve the result.
   */
  <ReturnType> Future<ReturnType> publish(Object event);

}
