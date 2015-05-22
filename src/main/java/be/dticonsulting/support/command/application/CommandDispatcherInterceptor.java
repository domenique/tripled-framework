package be.dticonsulting.support.command.application;

/**
 * Interface which defines an interceptor which is being called by a CommandDispatcher when executing a command.
 * <p>
 * The interceptor contract defines one method: intercept(). This method is invoked by the InterceptorChain which is
 * passed into the method as the first argument. Implementations are required to call the InterceptorChain.proceed() method
 * to complete the chain. Note that any interceptor in the chain should handle exceptions by either swallowing them in order to complete
 * the chain or to rethrow them to jump out of the chain.
 * <p>
 * The command passed to the intercept method is the command that will eventually be executed.
 */
public interface CommandDispatcherInterceptor {

  /**
   * Intercepts the command dispatching.
   * <p>
   * This method works similarly to an Around Advice. Implementations are supposed to call the chain.proceed() method to
   * advance further in the chain.
   *
   * @param chain        The InterceptorChain which is being applied. The implementation is supposed to call the proceed()
   *                     method when it wishes to advance in the chain
   * @param command      The command which will eventually be executed.
   * @param <ReturnType> The return type of the command and by consequence this Interceptor.
   * @return The return object from the chain.proceed() command or, depending on the implementation, some other instance of the ReturnType.
   * @throws Throwable When an exception occurred.
   */
  <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Command<ReturnType> command) throws Throwable;

}