package eu.tripledframework.eventbus;

public class HelloCommand {

  private String name;

  public HelloCommand(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
