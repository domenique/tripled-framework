package be.dticonsulting.command;

/**
 * Interface to indicate that the implementer is a Command.
 * <p/>
 * A command has a single method called execute and is being executed by a {@link CommandDispatcher}. If the
 * command should pass a validation step before being executed, then it should also implement the
 * {@link Validateable} interface.
 * <p/>
 * The CommandDispatcher is responsible of executing the validation step if there is any, and then the command itself.
 *
 * @param <ReturnType> The Type of the response object.
 */
public interface Command<ReturnType> {

  /**
   * Method called by the CommandDispatcher to execute the command.
   *
   * @return the response.
   */
  ReturnType execute();
}
