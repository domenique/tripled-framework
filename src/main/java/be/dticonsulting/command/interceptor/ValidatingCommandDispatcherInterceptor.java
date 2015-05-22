package be.dticonsulting.command.interceptor;

import be.dticonsulting.command.Command;
import be.dticonsulting.command.CommandDispatcherInterceptor;
import be.dticonsulting.command.InterceptorChain;
import be.dticonsulting.command.callback.CommandValidationException;
import be.dticonsulting.command.Validateable;

public class ValidatingCommandDispatcherInterceptor implements CommandDispatcherInterceptor {

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Command<ReturnType> command) throws Throwable {
    if (!isValid(command)) {
      throw new CommandValidationException("The command failed the validation step.");
    } else {
      return chain.proceed();
    }
  }

  private boolean isValid(Command command) {
    boolean isValid = true;
    if (shouldPerformValidation(command)) {
      isValid = ((Validateable) command).validate();
    }
    return isValid;
  }

  private boolean shouldPerformValidation(Command command) {
    return command instanceof Validateable;
  }
}
