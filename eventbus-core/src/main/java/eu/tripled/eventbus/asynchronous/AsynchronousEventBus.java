package eu.tripled.eventbus.asynchronous;

import eu.tripled.eventbus.EventBusInterceptor;
import eu.tripled.eventbus.EventCallback;
import eu.tripled.eventbus.synchronous.SynchronousEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * An Implementation of the CommandDispatcher which executes the command in an asynchronous fashion.
 * This implementation uses a ThreadPool to publish events to separate threads.
 */
public class AsynchronousEventBus extends SynchronousEventBus {

  private final Logger logger = LoggerFactory.getLogger(AsynchronousEventBus.class);

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
  protected <ReturnType> void publishInternal(Object message, EventCallback<ReturnType> callback) {
    executor.execute(new RunnableCommand<>(message, callback));
  }

  private class RunnableCommand<ReturnType> implements Runnable {

    private final Object message;
    private final EventCallback<ReturnType> callback;

    public RunnableCommand(Object message, EventCallback<ReturnType> callback) {
      this.message = message;
      this.callback = callback;
    }

    @Override
    public void run() {
      AsynchronousEventBus.super.publishInternal(message, callback);
    }
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }
}
