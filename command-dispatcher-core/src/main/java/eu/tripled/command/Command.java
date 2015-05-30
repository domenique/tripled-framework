package eu.tripled.command;

public class Command<T> {

  private T body;

  public Command(T body) {
    this.body = body;
  }

  public T getBody() {
    return body;
  }
}
