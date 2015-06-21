package eu.tripledframework.eventbus.interceptor;

import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.InterceptorChain;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

public class ValidatingEventBusInterceptor implements EventBusInterceptor {

  private Validator validator;

  public ValidatingEventBusInterceptor(Validator validator) {
    this.validator = validator;
  }

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event) throws Exception {
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
