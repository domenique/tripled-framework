/*
 * Copyright 2022 TripleD framework.
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

import eu.tripledframework.eventbus.internal.infrastructure.interceptor.CommandValidationException;
import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@ControllerAdvice
public class GlobalExceptionControllerAdvice {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionControllerAdvice.class);

  @ExceptionHandler
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public List<ErrorMessage> handleExecutionException(ExecutionException exception) throws Throwable {
    // we simply rethrow the cause.
    List<ErrorMessage> errorMessages = new ArrayList<>();
    if (exception.getCause() instanceof CommandValidationException) {
      errorMessages.addAll(handleValidationError((CommandValidationException) exception.getCause()));
    } else {
      LOGGER.error("The execution failed with an uncaught exception.", exception);
      errorMessages.add(new ErrorMessage("The execution failed with an uncaught exception."));
    }
    return errorMessages;
  }

  private List<ErrorMessage> handleValidationError(CommandValidationException exception) {
    List<ErrorMessage> errorMessages = new ArrayList<>();
    for (var objectConstraintViolation : exception.getConstraintViolations()) {
      var errorMessage = new ErrorMessage(objectConstraintViolation.getMessage());

      errorMessages.add(errorMessage);
    }

    return errorMessages;
  }
}
