package eu.tripled.eventbus;

import eu.tripled.eventbus.event.Event;

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
   * Called when the event did not pass validation.
   *
   * @param event The command which failed.
   */
  void onValidationFailure(Event event);

  /**
   * Called when the event failed.
   *
   * @param exception The exception received from executing the event handling.
   */
  void onFailure(Throwable exception);

}
