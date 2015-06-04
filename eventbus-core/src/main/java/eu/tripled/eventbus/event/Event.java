package eu.tripled.eventbus.event;

public class Event<T> {

  private T body;

  public Event(T body) {
    this.body = body;
  }

  public T getBody() {
    return body;
  }
}
