package eu.tripledframework.demo.presentation;

import eu.tripledframework.eventbus.domain.interceptor.CommandValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@ControllerAdvice
public class GlobalExceptionControllerAdvice {

  @ExceptionHandler
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public List<ErrorMessage> handleExecutionException(ExecutionException exception) throws Throwable {
    // we simply rethrow the cause.
    List<ErrorMessage> errorMessages = new ArrayList<>();
    if (exception.getCause() instanceof CommandValidationException) {
      errorMessages.addAll(handleValidationError((CommandValidationException) exception.getCause()));
    } else {
      errorMessages.add(new ErrorMessage("The execution failed with an uncaught exception."));
    }
    return errorMessages;
  }

  private List<ErrorMessage> handleValidationError(CommandValidationException exception) {
    List<ErrorMessage> errorMessages = new ArrayList<>();
    for (ConstraintViolation<Object> objectConstraintViolation : exception.getConstraintViolations()) {
      ErrorMessage errorMessage = new ErrorMessage(objectConstraintViolation.getMessage());

      errorMessages.add(errorMessage);
    }

    return errorMessages;
  }
}
