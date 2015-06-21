package eu.tripledframework.eventbus.command;

public class HelloCommand {

  private String name;

  public HelloCommand(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
