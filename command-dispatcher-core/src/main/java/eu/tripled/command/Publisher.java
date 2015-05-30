package eu.tripled.command;

/**
 * The basic contract of the CommandDispatcher.
 * <p>
 * A CommandDispatcher is responsible for executing commands. The dispatcher can, depending on the implementation execute the commandMessage in
 * synchronous or asynchronous fashion.
 */
public interface Publisher {

  /**
   * Dispatches the given commandMessage and invokes the given callback with either success or failure.
   *
   * @param commandMessage The commandMessage to publish.
   * @param <ReturnType>   The return type of the commandMessage.
   */
  <ReturnType> void publish(Object commandMessage, EventCallback<ReturnType> callback);

  /**
   * Dispatches a commandMessage without returning any feedback.
   *
   * @param commandMessage      The commandMessage to publish.
   * @param <ReturnType> The return type of the commandMessage.
   */
  <ReturnType> void publish(Object commandMessage);
}
