package eu.tripled.demo;

public class SaidHelloDomainEvent {

  private String name;

  public SaidHelloDomainEvent(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
