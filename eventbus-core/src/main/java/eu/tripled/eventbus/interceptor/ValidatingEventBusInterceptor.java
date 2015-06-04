package eu.tripled.eventbus.interceptor;

import eu.tripled.eventbus.event.Event;
import eu.tripled.eventbus.EventBusInterceptor;
import eu.tripled.eventbus.InterceptorChain;
import eu.tripled.eventbus.callback.CommandValidationException;
import eu.tripled.eventbus.Validateable;

public class ValidatingEventBusInterceptor implements EventBusInterceptor {

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Event event) throws Throwable {
    if (!isValid(event)) {
      throw new CommandValidationException("The command failed the validation step.");
    } else {
      return chain.proceed();
    }
  }

  private boolean isValid(Event event) {
    boolean isValid = true;
    if (shouldPerformValidation(event)) {
      isValid = ((Validateable) event.getBody()).validate();
    }
    return isValid;
  }

  private boolean shouldPerformValidation(Event event) {
    return event.getBody() instanceof Validateable;
  }
}
