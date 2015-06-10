package eu.tripled.eventbus.synchronous;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An EventHandlerInvoker is responsible for invoking all EventHandler instances registered to receive events for a given type.
 * <p>
 * For every event type, there should be 1 EventHandlerInvoker. This invoker makes a distinction between event handlers
 * having a return type and void event handlers. The EventHandler with a return type is considered the primary EventHandler and is invoked first.
 * The result from the primary EventHandler is then stored, and only returned after invoking all other EventHandler instances.
 * <p></p>
 * When adding EventHandler instances to this invoker object, the implementation will refuse multiple EventHandler instances with a return type.
 * This is necessary because otherwise we should somehow aggregate all responses from all EventHandlers and return that. Internally, the EventHandler instances
 * are stored in a set, this implies that adding duplicates will not have any effect, they will be discarded by the Set.
 */
public class EventHandlerInvoker {

  private Set<ObjectMethodPair> objectMethodPairs;
  private ObjectMethodPair objectMethodPairWithReturnType;

  public EventHandlerInvoker(Object eventHandler, Method method) {
    objectMethodPairs = new HashSet<>();

    addObjectMethodPair(eventHandler, method);
  }

  public void addObjectMethodPair(Object eventHandler, Method method) {
    ObjectMethodPair objectMethodPair = new ObjectMethodPair(eventHandler, method);
    if (objectMethodPair.hasReturnType()) {
      if (objectMethodPairWithReturnType == null || objectMethodPairWithReturnType.equals(objectMethodPair)) {
        objectMethodPairWithReturnType = objectMethodPair;
      } else {
        throw new DuplicateEventHandlerRegistrationException(
            String.format("Cannot register method %s on %s. An event handler with response type is already registered.",
                method.getName(), eventHandler.getClass().getSimpleName()));
      }
    } else {
      objectMethodPairs.add(objectMethodPair);
    }
  }

  public Object invoke(Object object) {
    Object response = null;
    if (objectMethodPairWithReturnType != null) {
      response = invokeInternal(object, objectMethodPairWithReturnType);
    }

    for (ObjectMethodPair current : objectMethodPairs) {
      invokeInternal(object, current);
    }
    return response;
  }

  private Object invokeInternal(Object object, ObjectMethodPair omp) {
    try {
      return omp.getMethod().invoke(omp.getEventHandler(), object);
    } catch (InvocationTargetException | IllegalAccessException e) {
      String errorMsg = String.format("Could not invoke EventHandler method %s on %s", omp.getMethod().getName(), omp.getEventHandler().getClass().getSimpleName());
      throw new EventHandlerInvocationException(errorMsg, e);
    }
  }

  private class ObjectMethodPair {
    private Object eventHandler;
    private Method method;

    public ObjectMethodPair(Object eventHandler, Method method) {
      this.eventHandler = eventHandler;
      this.method = method;
    }

    public Object getEventHandler() {
      return eventHandler;
    }

    public Method getMethod() {
      return method;
    }

    public boolean hasReturnType() {
      return !method.getReturnType().getName().equals("void");
    }

    @Override
    public int hashCode() {
      return Objects.hash(eventHandler, method);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      final ObjectMethodPair other = (ObjectMethodPair) obj;
      return Objects.equals(this.eventHandler, other.eventHandler)
          && Objects.equals(this.method, other.method);
    }
  }


}
