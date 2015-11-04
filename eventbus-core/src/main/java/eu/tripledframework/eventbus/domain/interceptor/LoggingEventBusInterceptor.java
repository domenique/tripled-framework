package eu.tripledframework.eventbus.domain.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.InterceptorChain;

public class LoggingEventBusInterceptor implements EventBusInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEventBusInterceptor.class);

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event) {
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