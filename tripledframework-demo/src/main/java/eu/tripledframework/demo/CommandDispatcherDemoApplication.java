package eu.tripledframework.demo;

import eu.tripledframework.eventbus.domain.annotation.EnableEventHandlerSupport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEventHandlerSupport(basePackage = "eu.tripledframework.demo")
public class CommandDispatcherDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(CommandDispatcherDemoApplication.class, args);
  }
}
