package eu.tripledframework.eventbus.domain.interceptor;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.InterceptorChain;

public class ValidatingEventBusInterceptor implements EventBusInterceptor {

  private Validator validator;

  public ValidatingEventBusInterceptor(Validator validator) {
    this.validator = validator;
  }

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event) {
    validate(event);
    return chain.proceed();
  }

  private void validate(Object event) {
    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(event);
    if (!constraintViolations.isEmpty()) {
      throw new CommandValidationException("The command failed the validation step.", constraintViolations);
    }
  }

}
