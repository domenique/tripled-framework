package be.dticonsulting.command;

/**
 * An interface to indicate that the implementing object is a callback.
 *
 * @param <ReturnType> The ReturnType of the command.
 */
public interface CommandCallback<ReturnType> {

  /**
   * Called by CommandDispatcher when the command is executed successfully.
   *
   * @param result The result from the command.
   */
  void onSuccess(ReturnType result);

  /**
   * Called when the command did not pass validation.
   *
   * @param command The command which failed.
   */
  void onValidationFailure(Command<ReturnType> command);

  /**
   * Called when the command failed.
   *
   * @param exception The exception received from executing the command.
   */
  void onFailure(Throwable exception);

}
