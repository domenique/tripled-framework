package eu.tripledframework.eventbus.domain.invoker;

import java.lang.reflect.Method;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class SimpleHandlerInvokerTest {

  @Test
  public void verifyEquals() throws Exception {
    EqualsVerifier.forClass(SimpleHandlerInvoker.class)
        .withPrefabValues(Method.class, SimpleHandlerInvoker.class.getDeclaredMethod("handles", Class.class),
            SimpleHandlerInvoker.class.getDeclaredMethod("getEventType"))
        .verify();
  }
}