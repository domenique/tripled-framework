package eu.tripledframework.eventbus.domain;

/**
 * An interface to indicate that the implementing object is a callback when publishing events.
 * <p>
 * The Callback is called by the eventbus after executing all the interceptors and the command handler. If the
 * command is executed successfully the onSuccess(result) is called passing the command handlers result object if
 * it had a result. If the command failed by throwing an exception, the onFailure(exception) is called.
 * <p>
 * Please take care about what logic you put in a callback. This is no place to add additional business logic to the
 * command handlers. The execution of this callback is not part of any transaction and should only be used to perform
 * some UI logic without blocking the calling thread. This makes a callback an ideal place to inform clients through
 * some push technology like web-sockets, or ui updates in a swing application.
 * <p>
 * If you want to publish an event, and you don't want to block the calling thread but still perform some logic when
 * the event is processed, this is the way to do it. If you want to block the calling thread you should use the
 * FutureEventCallback which implements this callback interface and the adheres to the Future interface.
 *
 * @param <ReturnType> The ReturnType of the command.
 */
public interface EventCallback<ReturnType> {

  /**
   * Called by the EventBus when the command is executed successfully.
   *
   * @param result The result from the command.
   */
  void onSuccess(ReturnType result);

  /**
   * Called when the event failed.
   *
   * @param exception The exception received from executing the event handling.
   */
  void onFailure(RuntimeException exception);
}
