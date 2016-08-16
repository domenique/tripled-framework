package eu.tripledframework.demo.security;

import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.DefaultUnitOfWork;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityAwareUnitOfWork extends DefaultUnitOfWork {

  public SpringSecurityAwareUnitOfWork() {
    super();
    addData("SecurityContext", SecurityContextHolder.getContext());
  }
}
