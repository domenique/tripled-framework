package be.scp.command.application;

/**
 * Interface to indicate that the implementer is a Command.
 * <p/>
 * A command has a single method called execute and is being executed by a {@link be.scp.command.application.CommandDispatcher}. If the
 * command should pass a validation step before being executed, then it should also implement the
 * {@link be.scp.command.application.Validateable} interface.
 * <p/>
 * The CommandDispatcher is responsible of executing the validation step if there is any, and then the command itself.
 */
public interface Command {

  /**
   * Method called by the CommandDispatcher to execute the command.
   */
  void execute();
}
