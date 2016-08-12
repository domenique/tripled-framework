package eu.tripledframework.eventbus.internal.domain;

import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.SimpleInterceptorChainFactory;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InMemoryInvokerRepository;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.SimpleInvokerFactory;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.DefaultUnitOfWorkFactory;

import java.util.Collections;
import java.util.List;

abstract class AbstractEventBusTest {

  SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> interceptors) {
    return new SynchronousEventBus(new InMemoryInvokerRepository(), new SimpleInterceptorChainFactory(interceptors), Collections
        .singletonList(new SimpleInvokerFactory()), new DefaultUnitOfWorkFactory());
  }

  SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> interceptors, UnitOfWorkFactory unitOfWorkFactory) {
    return new SynchronousEventBus(new InMemoryInvokerRepository(), new SimpleInterceptorChainFactory(interceptors), Collections
        .singletonList(new SimpleInvokerFactory()), unitOfWorkFactory);
  }

  SynchronousEventBus createSynchronousEventBus(List<EventBusInterceptor> interceptors, List<InvokerFactory> invokerFactories) {
    return new SynchronousEventBus(new InMemoryInvokerRepository(), new SimpleInterceptorChainFactory(interceptors), invokerFactories,
        new DefaultUnitOfWorkFactory());
  }
}
