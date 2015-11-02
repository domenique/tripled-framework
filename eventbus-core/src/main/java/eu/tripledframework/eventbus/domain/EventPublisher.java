package eu.tripledframework.eventbus.domain;

import java.util.concurrent.Future;

/**
 * The basic contract of the EventBus when publishing events..
 * <p>
 * An EventBusPublisher is responsible for dispatching events. The EventBus can, depending on the implementation execute the event handling in a
 * synchronous or asynchronous fashion.
 */
public interface EventPublisher {

  /**
   * Dispatches the given Event and invokes the given callback with either success or failure.
   *
   * @param event The event to publish.
   * @param callback The callback to invoke upon completion.
   * @param <ReturnType>   The return type of the event handling.
   */
  <ReturnType> Future<ReturnType> publish(Object event, EventCallback<ReturnType> callback);

  /**
   * Dispatches an event without returning any feedback.
   *
   * @param event      The event to publish.
   * @param <ReturnType> The return type of the event handling.
   *
   * @return Future<ReturnType> A future that gives
   */
  <ReturnType> Future<ReturnType> publish(Object event);
}
