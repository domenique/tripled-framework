package eu.tripled.eventbus.asynchronous;

import eu.tripled.eventbus.event.Event;
import eu.tripled.eventbus.EventCallback;
import eu.tripled.eventbus.EventBusInterceptor;
import eu.tripled.eventbus.synchronous.SynchronousEventBus;

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
  protected <ReturnType> void dispatchInternal(Event event, EventCallback<ReturnType> callback) {
    executor.execute(new RunnableCommand<>(event, callback));
  }

  private class RunnableCommand<ReturnType> implements Runnable {

    private final Event event;
    private final EventCallback<ReturnType> callback;

    public RunnableCommand(Event event, EventCallback<ReturnType> callback) {
      this.event = event;
      this.callback = callback;
    }

    @Override
    public void run() {
      AsynchronousEventBus.super.dispatchInternal(event, callback);
    }
  }
}
