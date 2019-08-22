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
package eu.tripledframework.eventbus.internal.infrastructure.interceptor;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.HashSet;
import java.util.Set;

public class TestValidator implements Validator {

  private boolean isValidateCalled;
  private boolean shouldFail;

  public void shouldFailNextCall(boolean shouldFail) {
    this.shouldFail = shouldFail;
  }

  @Override
  public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
    isValidateCalled = true;
    HashSet<ConstraintViolation<T>> constraintViolations = new HashSet<>();
    if (shouldFail) {
      constraintViolations.add(new ConstraintViolation<T>() {
        @Override
        public String getMessage() {
          return null;
        }

        @Override
        public String getMessageTemplate() {
          return null;
        }

        @Override
        public T getRootBean() {
          return null;
        }

        @Override
        public Class<T> getRootBeanClass() {
          return null;
        }

        @Override
        public Object getLeafBean() {
          return null;
        }

        @Override
        public Object[] getExecutableParameters() {
          return new Object[0];
        }

        @Override
        public Object getExecutableReturnValue() {
          return null;
        }

        @Override
        public Path getPropertyPath() {
          return null;
        }

        @Override
        public Object getInvalidValue() {
          return null;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
          return null;
        }

        @Override
        public <U> U unwrap(Class<U> type) {
          return null;
        }
      });
    }
    return constraintViolations;
  }

  @Override
  public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
    return new HashSet<>();
  }

  @Override
  public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
    return new HashSet<>();
  }

  @Override
  public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
    return null;
  }

  @Override
  public <T> T unwrap(Class<T> type) {
    return null;
  }

  @Override
  public ExecutableValidator forExecutables() {
    return null;
  }
}
