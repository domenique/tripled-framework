package eu.tripled.command.interceptor;

import eu.tripled.command.Command;
import eu.tripled.command.EventBusInterceptor;
import eu.tripled.command.InterceptorChain;
import eu.tripled.command.callback.CommandValidationException;
import eu.tripled.command.Validateable;

public class ValidatingEventBusInterceptor implements EventBusInterceptor {

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Command command) throws Throwable {
    if (!isValid(command)) {
      throw new CommandValidationException("The command failed the validation step.");
    } else {
      return chain.proceed();
    }
  }

  private boolean isValid(Command command) {
    boolean isValid = true;
    if (shouldPerformValidation(command)) {
      isValid = ((Validateable) command.getBody()).validate();
    }
    return isValid;
  }

  private boolean shouldPerformValidation(Command command) {
    return command.getBody() instanceof Validateable;
  }
}
