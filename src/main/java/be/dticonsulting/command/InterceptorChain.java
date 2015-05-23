package be.dticonsulting.command;

/**
 * The interceptorChain is used by an interceptor to proceed the chain.
 *
 * @param <ReturnType> The Type of the return object of the command.
 */
public interface InterceptorChain<ReturnType> {

  /**
   * Method which is supposed to be called by the interceptor the advance in the chain.
   *
   * @return The return object of the command.
   * @throws Throwable The exception received from the command if it failed.
   */
  ReturnType proceed() throws Throwable;
}
