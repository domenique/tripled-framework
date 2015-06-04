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
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Event event) throws Throwable {
    if (!isValid(event)) {
      throw new CommandValidationException("The command failed the validation step.");
    } else {
      return chain.proceed();
    }
  }

  private boolean isValid(Event event) {
    Set<ConstraintViolation<Object>> validate = validator.validate(event.getBody());
    // TODO: we need to provide these exceptions through the exception?
    boolean isValid = validate.isEmpty();

    return isValid;
  }

}
