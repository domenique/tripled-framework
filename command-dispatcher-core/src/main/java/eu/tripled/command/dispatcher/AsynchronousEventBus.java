package eu.tripled.command.dispatcher;

import eu.tripled.command.Command;
import eu.tripled.command.EventCallback;
import eu.tripled.command.EventBusInterceptor;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * An Implementation of the CommandDispatcher which executes the command in an asynchronous fashion.
 * This implementation uses a ThreadPool to publish events to separate threads.
 */
public class AsynchronousEventBus extends SynchronousEventBus {

  private Executor executor;

  public AsynchronousEventBus() {
    super();
    this.executor = Executors.newCachedThreadPool();
  }

  public AsynchronousEventBus(Executor executor) {
    super();
    this.executor = executor;
  }

  public AsynchronousEventBus(List<EventBusInterceptor> interceptors, Executor executor) {
    super(interceptors);
    this.executor = executor;
  }

  @Override
  protected <ReturnType> void dispatchInternal(Command command, EventCallback<ReturnType> callback) {
    executor.execute(new RunnableCommand<>(command, callback));
  }

  private class RunnableCommand<ReturnType> implements Runnable {

    private final Command command;
    private final EventCallback<ReturnType> callback;

    public RunnableCommand(Command command, EventCallback<ReturnType> callback) {
      this.command = command;
      this.callback = callback;
    }

    @Override
    public void run() {
      AsynchronousEventBus.super.dispatchInternal(command, callback);
    }
  }
}
