package eu.tripledframework.eventstore.infrastructure;

import eu.tripledframework.eventstore.domain.ConstructionAware;
import eu.tripledframework.eventstore.domain.DomainEvent;
import eu.tripledframework.eventstore.domain.ObjectConstructor;
import eu.tripledframework.eventstore.domain.annotation.ConstructionHandler;
import eu.tripledframework.eventstore.domain.annotation.EP;
import org.apache.commons.jxpath.JXPathContext;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.reflections.ReflectionUtils.withAnnotation;

/**
 * A Class which is capable of reconstructing  objects using events.
 * <p>
 * This class will use reflection and the {@link ConstructionHandler} annotation to construct an object
 * using events.
 */
public class ReflectionObjectConstructor<T> implements ObjectConstructor<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionObjectConstructor.class);

  private Class<T> targetClass;

  public ReflectionObjectConstructor(Class<T> targetClass) {
    this.targetClass = targetClass;
  }

  @Override
  public T construct(Collection<DomainEvent> events) {
    if (events == null || events.isEmpty()) {
      throw new IllegalArgumentException("At least one event should be provided.");
    }

    DomainEvent firstEvent = events.stream().findFirst().get();
    T instance = createInstance(firstEvent);
    LOGGER.debug("Created new {} with event {}", instance.getClass().getSimpleName(), firstEvent);

    events.stream()
        .skip(1)
        .forEach(p -> {
          applyDomainEvent(instance, p);
          LOGGER.debug("Applied {}", p);
        });

    // Invoke the postConstruct on the object if it's ConstructionAware.
    if (instance instanceof ConstructionAware) {
      ((ConstructionAware) instance).postConstruct();
    }

    return instance;
  }

  private T createInstance(DomainEvent event) {
    Constructor constructor = getEventHandlerConstructor(event);
    if (constructor != null) {
      try {
        constructor.setAccessible(true);
        Object[] parameters = getParametersValues(constructor.getParameterAnnotations(), event);
        return (T) constructor.newInstance(parameters);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw new AggregateRootReconstructionException(String.format("Could not create object using constructor %s", constructor), e);
      }
    } else {
      throw new AggregateRootReconstructionException(String.format("Could not find a suitable constructor for event %s", event));
    }
  }

  private void applyDomainEvent(T instance, DomainEvent event) {
    Method method = getEventHandlerMethods(event);
    if (method != null) {
      try {
        method.setAccessible(true);
        Object[] parameters = getParametersValues(method.getParameterAnnotations(), event);
        method.invoke(instance, parameters);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new AggregateRootReconstructionException(String.format("Could not apply event %s to instance %s", event, instance), e);
      }
    } else {
      throw new AggregateRootReconstructionException(String.format("Could not find a suitable method for event %s", event));
    }
  }

  private Method getEventHandlerMethods(DomainEvent event) {
    Set<Method> methods = ReflectionUtils.getAllMethods(targetClass, withAnnotation(ConstructionHandler.class));
    for (Method method : methods) {
      ConstructionHandler annotation = method.getAnnotation(ConstructionHandler.class);
      if (annotation.value().equals(event.getClass())) {
        return method;
      }
    }
    return null;
  }


  private Constructor getEventHandlerConstructor(DomainEvent event) {
    Set<Constructor> constructors = ReflectionUtils.getConstructors(targetClass, withAnnotation(ConstructionHandler.class));
    for (Constructor constructor : constructors) {
      ConstructionHandler constructionHandlerAnnotation = (ConstructionHandler) constructor.getAnnotation(ConstructionHandler.class);
      if (constructionHandlerAnnotation.value().equals(event.getClass())) {
        return constructor;
      }
    }
    return null;
  }

  private Object[] getParametersValues(Annotation[][] parameterAnnotations, DomainEvent event) {
    List<Object> params = new ArrayList<>();

    for (Annotation[] annotations : parameterAnnotations) {
      for (Annotation annotation : annotations) {
        if (annotation.annotationType().equals(EP.class)) {
          EP eventParam = (EP) annotation;
          params.add(getValue(eventParam.value(), event));
        }
      }
    }
    return params.toArray(new Object[params.size()]);
  }

  private Object getValue(String expr, DomainEvent event) {
    JXPathContext context = JXPathContext.newContext(event);
    return context.getValue(expr);
  }
}
