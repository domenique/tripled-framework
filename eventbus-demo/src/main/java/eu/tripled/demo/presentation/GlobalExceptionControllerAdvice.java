package eu.tripled.demo.presentation;

import eu.tripled.eventbus.interceptor.CommandValidationException;
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
    if (exception.getCause() instanceof CommandValidationException) {
      return handleValidationError((CommandValidationException) exception.getCause());
    } else {
      return new ArrayList<>();
    }
  }

  private List<ErrorMessage> handleValidationError(CommandValidationException exception) {
    List<ErrorMessage> errorMessages = new ArrayList<>();
    for (ConstraintViolation<Object> objectConstraintViolation : exception.getConstraintViolations()) {
      ErrorMessage errorMessage = new ErrorMessage();
      errorMessage.setMessage(objectConstraintViolation.getMessage());

      errorMessages.add(errorMessage);
    }

    return errorMessages;
  }
}
