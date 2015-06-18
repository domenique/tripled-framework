package eu.tripled.eventbus.interceptor;

import eu.tripled.eventbus.EventBusInterceptor;
import eu.tripled.eventbus.InterceptorChain;
import eu.tripled.eventbus.callback.CommandValidationException;
import eu.tripled.eventbus.event.Event;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

public class ValidatingEventBusInterceptor implements EventBusInterceptor {

  private Validator validator;

  public ValidatingEventBusInterceptor(Validator validator) {
    this.validator = validator;
  }

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Event event) throws Exception {
    validate(event);
    return chain.proceed();
  }

  private void validate(Event event) {
    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(event.getBody());
    if (!constraintViolations.isEmpty()) {
      throw new CommandValidationException("The command failed the validation step.", constraintViolations);
    }

  }

}
