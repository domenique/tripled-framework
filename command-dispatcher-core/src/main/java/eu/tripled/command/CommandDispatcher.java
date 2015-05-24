package eu.tripled.command;

/**
 * The basic contract of the CommandDispatcher.
 * <p>
 * A CommandDispatcher is responsible for executing commands. The dispatcher can, depending on the implementation execute the command in
 * synchronous or asynchronous fashion.
 */
public interface CommandDispatcher {

  /**
   * Dispatches the given command and invokes the given callback with either success or failure.
   *
   * @param command      The command to dispatch.
   * @param <ReturnType> The return type of the command.
   */
  <ReturnType> void dispatch(Command<ReturnType> command, CommandCallback<ReturnType> callback);

  /**
   * Dispatches a command without returning any feedback.
   *
   * @param command      The command to dispatch.
   * @param <ReturnType> The return type of the command.
   */
  <ReturnType> void dispatch(Command<ReturnType> command);
}
