package be.dticonsulting.command.dispatcher;

import be.dticonsulting.command.Command;
import be.dticonsulting.command.CommandCallback;
import be.dticonsulting.command.CommandDispatcherInterceptor;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An Implementation of the CommandDispatcher which executes the command in an asynchronous fashion.
 * This implementation uses a ThreadPool to dispatch events to separate threads.
 */
public class AsynchronousCommandDispatcher extends SynchronousCommandDispatcher {

  private Executor executor;

  public AsynchronousCommandDispatcher() {
    super();
    this.executor = Executors.newCachedThreadPool();
  }

  public AsynchronousCommandDispatcher(Executor executor) {
    super();
    this.executor = executor;
  }

  public AsynchronousCommandDispatcher(List<CommandDispatcherInterceptor> interceptors, ExecutorService executor) {
    super(interceptors);
    this.executor = executor;
  }


  @Override
  protected <ReturnType> void dispatchInternal(Command<ReturnType> command, CommandCallback<ReturnType> callback) {
    executor.execute(new RunnableCommand<>(command, callback));
  }

  private class RunnableCommand<ReturnType> implements Runnable {

    private final Command<ReturnType> command;
    private final CommandCallback<ReturnType> callback;

    public RunnableCommand(Command<ReturnType> command, CommandCallback<ReturnType> callback) {
      this.command = command;
      this.callback = callback;
    }

    @Override
    public void run() {
      AsynchronousCommandDispatcher.super.dispatchInternal(command, callback);
    }
  }
}
