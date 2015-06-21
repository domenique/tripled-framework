package eu.tripledframework.eventbus.domain.interceptor;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class CommandValidationException extends RuntimeException {

  private final Set<ConstraintViolation<Object>> constraintViolations;

  public CommandValidationException(String message, Set<ConstraintViolation<Object>> constraintViolations) {
    super(message);
    this.constraintViolations = constraintViolations;
  }

  public Set<ConstraintViolation<Object>> getConstraintViolations() {
    return constraintViolations;
  }
}
