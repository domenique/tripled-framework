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
package eu.tripledframework.eventbus.internal.infrastructure.interceptor;

import java.util.Set;


import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.internal.domain.InterceptorChain;
import eu.tripledframework.eventbus.internal.domain.UnitOfWork;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

public class ValidatingEventBusInterceptor implements EventBusInterceptor {

  private Validator validator;

  public ValidatingEventBusInterceptor(Validator validator) {
    this.validator = validator;
  }

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event, UnitOfWork unitOfWork) {
    validate(event);
    return chain.proceed();
  }

  private void validate(Object event) {
    var constraintViolations = validator.validate(event);
    if (!constraintViolations.isEmpty()) {
      throw new CommandValidationException("The command failed the validation step.", constraintViolations);
    }
  }

}
