package be.dticonsulting.support.command.application;

/**
 * The basic contract of the CommandDispatcher.
 * <p/>
 * A CommandDispatcher is responsible for executing commands. The dispatcher can, depending on the implementation execute the command in
 * synchronous or asynchronous fashion.
 */
public interface CommandDispatcher {

  /**
   * Dispatches the given command, returning a response object.
   * @param command
   * @param <ReturnType>
   * @return
   */
  <ReturnType> ReturnType dispatch(Command<ReturnType> command);
}
