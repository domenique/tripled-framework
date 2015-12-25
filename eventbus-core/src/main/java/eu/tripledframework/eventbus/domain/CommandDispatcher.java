package eu.tripledframework.eventbus.domain;

import java.util.concurrent.Future;

/**
 * The basic contract of the EventBus when publishing commands.
 * <p>
 * A command dispatcher is responsible for dispatching commands. The EventBus can, depending on the implementation
 * execute the command handling in a synchronous or asynchronous fashion.
 */
public interface CommandDispatcher {

  /**
   * Dispatches the given command and invokes the given callback with either success or failure.
   *
   * @param command      The command to dispatch.
   * @param callback     The callback to invoke upon completion.
   * @param <ReturnType> The return type of the command handling.
   */
  <ReturnType> void dispatch(Object command, EventCallback<ReturnType> callback);

  /**
   * Dispatches the given command and invokes the given callback with either success or failure.
   *
   * @param command      The command to dispatch.
   * @param <ReturnType> The return type of the command handling.
   * @return A future object to be used to retrieve the result.
   */
  <ReturnType> Future<ReturnType> dispatch(Object command);

}
