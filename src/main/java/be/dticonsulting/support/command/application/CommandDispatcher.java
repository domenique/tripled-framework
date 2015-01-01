package be.dticonsulting.support.command.application;

/**
 * The basic contract of the CommandDispatcher.
 * <p/>
 * A CommandDispatcher is responsible for executing commands. The dispatcher can, depending on the implementation execute the command in
 * synchronous or asynchronous fashion.
 */
public interface CommandDispatcher {

  /**
   * dispatches the command and executes it.
   *
   * @param command The command to execute.
   */
  void dispatch(Command command);
}
