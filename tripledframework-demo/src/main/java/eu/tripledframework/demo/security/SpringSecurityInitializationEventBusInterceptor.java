package eu.tripledframework.demo.security;

import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.internal.domain.InterceptorChain;
import eu.tripledframework.eventbus.internal.domain.UnitOfWork;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityInitializationEventBusInterceptor implements EventBusInterceptor {
  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event, UnitOfWork unitOfWork) {
    SecurityContextHolder.setContext((SecurityContext) unitOfWork.getData("SecurityContext"));
    return chain.proceed();
  }
}
