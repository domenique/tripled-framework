package eu.tripledframework.eventbus.interceptor;

import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.InterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEventBusInterceptor implements EventBusInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEventBusInterceptor.class);

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event) throws Exception {
    LOGGER.debug("Executing command {}", event.getClass().getSimpleName());
    try {
      ReturnType proceed = chain.proceed();
      LOGGER.debug("Finished executing command {}", event.getClass().getSimpleName());

      return proceed;
    } catch (Throwable ex) {
      LOGGER.debug("Command {} failed", event.getClass().getSimpleName());
      throw ex;
    }
  }
}