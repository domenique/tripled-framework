/*
 * Copyright 2015 TripleD framework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
