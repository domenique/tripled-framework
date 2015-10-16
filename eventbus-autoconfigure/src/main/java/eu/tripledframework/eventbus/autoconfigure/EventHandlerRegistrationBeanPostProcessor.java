package eu.tripledframework.eventbus.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import eu.tripledframework.eventbus.domain.EventSubscriber;
import eu.tripledframework.eventbus.domain.annotation.EventHandler;

public class EventHandlerRegistrationBeanPostProcessor implements BeanPostProcessor {

  private EventSubscriber eventSubscriber;

  @Autowired
  public EventHandlerRegistrationBeanPostProcessor(EventSubscriber eventSubscriber) {
    this.eventSubscriber = eventSubscriber;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    EventHandler annotation = bean.getClass().getAnnotation(EventHandler.class);
    if (annotation != null) {
      eventSubscriber.subscribe(bean);
    }

    return bean;
  }
}
