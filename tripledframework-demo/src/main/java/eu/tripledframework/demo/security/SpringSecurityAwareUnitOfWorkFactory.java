package eu.tripledframework.demo.security;

import eu.tripledframework.eventbus.internal.domain.UnitOfWork;
import eu.tripledframework.eventbus.internal.domain.UnitOfWorkFactory;

public class SpringSecurityAwareUnitOfWorkFactory implements UnitOfWorkFactory {
  @Override
  public UnitOfWork create() {
    return new SpringSecurityAwareUnitOfWork();
  }
}
