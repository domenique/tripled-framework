package eu.tripled.demo;

import eu.tripled.eventbus.annotation.EnableEventHandlerSupport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEventHandlerSupport(basePackage = "eu.tripled.demo")
public class CommandDispatcherDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(CommandDispatcherDemoApplication.class, args);
  }
}
