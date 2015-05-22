package be.dticonsulting.command.interceptor;

import be.dticonsulting.command.Command;
import be.dticonsulting.command.CommandDispatcherInterceptor;
import be.dticonsulting.command.InterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingCommandDispatcherInterceptor implements CommandDispatcherInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingCommandDispatcherInterceptor.class);

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Command<ReturnType> command) throws Throwable {
    LOGGER.debug("Executing command {}", command.getClass().getSimpleName());
    try {
      ReturnType proceed = chain.proceed();
      LOGGER.debug("Finished executing command {}", command.getClass().getSimpleName());

      return proceed;
    } catch (Throwable ex) {
      LOGGER.debug("Command {} failed", command.getClass().getSimpleName());
      throw ex;
    }
  }
}