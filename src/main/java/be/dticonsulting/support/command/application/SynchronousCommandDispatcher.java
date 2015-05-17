package be.dticonsulting.support.command.application;

import be.dticonsulting.support.command.application.callback.CommandValidationException;
import be.dticonsulting.support.command.application.callback.ExceptionThrowingCommandCallback;
import be.dticonsulting.support.command.application.interceptor.LoggingCommandDispatcherInterceptor;
import be.dticonsulting.support.command.application.interceptor.ValidatingCommandDispatcherInterceptor;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Synchronous implementation of the CommandDispatcher.
 */
public class SynchronousCommandDispatcher implements CommandDispatcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(SynchronousCommandDispatcher.class);
  private List<CommandDispatcherInterceptor> interceptors = new ArrayList<>();

  public SynchronousCommandDispatcher() {
    this.interceptors = new ArrayList<>();
    interceptors.add(0, new LoggingCommandDispatcherInterceptor());
    interceptors.add(1, new ValidatingCommandDispatcherInterceptor());
  }

  public SynchronousCommandDispatcher(List<CommandDispatcherInterceptor> interceptors) {
    interceptors.add(0, new LoggingCommandDispatcherInterceptor());
    interceptors.add(1, new ValidatingCommandDispatcherInterceptor());
    this.interceptors = interceptors;
  }

  @Override
  public <ReturnType> void dispatch(Command<ReturnType> command, CommandCallback<ReturnType> callback) {
    Preconditions.checkArgument(command != null, "The command cannot be null.");
    Preconditions.checkArgument(callback != null, "The callback cannot be null.");
    LOGGER.debug("Received a command to dispatch: {}", command.getClass().getSimpleName());

    InterceptorChain<ReturnType> chain = new InterceptorChain<>(command, interceptors);
    try {
      ReturnType response = chain.proceed();
      callback.onSuccess(response);
    } catch (CommandValidationException validationEx) {
      callback.onValidationFailure(command);
    } catch (Throwable exception) {
      callback.onFailure(exception);
    }

    LOGGER.debug("Finished executing command {}", command.getClass().getSimpleName());
  }

  @Override
  public <ReturnType> void dispatch(Command<ReturnType> command) {
    dispatch(command, new ExceptionThrowingCommandCallback<>());
  }


}
