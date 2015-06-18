package eu.tripled.eventbus.interceptor;

import eu.tripled.eventbus.event.Event;
import eu.tripled.eventbus.EventBusInterceptor;
import eu.tripled.eventbus.InterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEventBusInterceptor implements EventBusInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEventBusInterceptor.class);

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Event event) throws Exception {
    LOGGER.debug("Executing command {}", event.getBody().getClass().getSimpleName());
    try {
      ReturnType proceed = chain.proceed();
      LOGGER.debug("Finished executing command {}", event.getBody().getClass().getSimpleName());

      return proceed;
    } catch (Throwable ex) {
      LOGGER.debug("Command {} failed", event.getBody().getClass().getSimpleName());
      throw ex;
    }
  }
}