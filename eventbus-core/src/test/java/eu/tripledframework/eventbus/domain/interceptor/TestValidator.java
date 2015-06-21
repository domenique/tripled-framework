package eu.tripledframework.eventbus.domain.interceptor;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.HashSet;
import java.util.Set;

public class TestValidator implements Validator {

  public boolean isValidateCalled;
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
