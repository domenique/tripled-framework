package eu.tripled.demo.application;

public class HelloResponse {
  private String message;

  public HelloResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
