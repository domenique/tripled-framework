package eu.tripled.eventbus;

/**
 * An interface to indicate that the implementing object is a callback.
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
  void onFailure(Throwable exception);

}
