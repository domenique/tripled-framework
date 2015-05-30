package eu.tripled.command.interceptor;

import eu.tripled.command.Command;
import eu.tripled.command.EventBusInterceptor;
import eu.tripled.command.InterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEventBusInterceptor implements EventBusInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEventBusInterceptor.class);

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Command command) throws Throwable {
    LOGGER.debug("Executing command {}", command.getBody().getClass().getSimpleName());
    try {
      ReturnType proceed = chain.proceed();
      LOGGER.debug("Finished executing command {}", command.getBody().getClass().getSimpleName());

      return proceed;
    } catch (Throwable ex) {
      LOGGER.debug("Command {} failed", command.getBody().getClass().getSimpleName());
      throw ex;
    }
  }
}