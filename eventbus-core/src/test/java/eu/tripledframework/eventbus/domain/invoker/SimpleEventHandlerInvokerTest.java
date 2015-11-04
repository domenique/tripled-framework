package eu.tripledframework.eventbus.domain.invoker;

import java.lang.reflect.Method;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class SimpleEventHandlerInvokerTest {

  @Test
  public void verifyEquals() throws Exception {
    EqualsVerifier.forClass(SimpleEventHandlerInvoker.class)
        .withPrefabValues(Method.class, SimpleEventHandlerInvoker.class.getDeclaredMethod("handles", Class.class),
            SimpleEventHandlerInvoker.class.getDeclaredMethod("getEventType"))
        .verify();
  }
}