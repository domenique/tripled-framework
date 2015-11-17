package eu.tripledframework.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import eu.tripledframework.eventbus.domain.annotation.EnableEventHandlerSupport;

@SpringBootApplication
@EnableEventHandlerSupport(basePackage = "eu.tripledframework.demo")
public class CommandDispatcherDemoApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(CommandDispatcherDemoApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(CommandDispatcherDemoApplication.class);
  }
}
