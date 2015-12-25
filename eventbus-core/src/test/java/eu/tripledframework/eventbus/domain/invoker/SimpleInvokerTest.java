package eu.tripledframework.eventbus.domain.invoker;

import java.lang.reflect.Method;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class SimpleInvokerTest {

  @Test
  public void verifyEquals() throws Exception {
    EqualsVerifier.forClass(SimpleInvoker.class)
        .withPrefabValues(Method.class, SimpleInvoker.class.getDeclaredMethod("handles", Class.class),
            SimpleInvoker.class.getDeclaredMethod("hasReturnType"))
        .verify();
  }
}