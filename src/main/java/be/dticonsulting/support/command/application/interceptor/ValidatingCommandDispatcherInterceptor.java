package be.dticonsulting.support.command.application.interceptor;

import be.dticonsulting.support.command.application.Command;
import be.dticonsulting.support.command.application.CommandDispatcherInterceptor;
import be.dticonsulting.support.command.application.InterceptorChain;
import be.dticonsulting.support.command.application.callback.CommandValidationException;
import be.dticonsulting.support.command.application.Validateable;

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
