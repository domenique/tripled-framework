package eu.tripledframework.demo.presentation;

import eu.tripledframework.demo.application.HelloCommand;
import eu.tripledframework.demo.application.HelloResponse;
import eu.tripledframework.eventbus.EventPublisher;
import eu.tripledframework.eventbus.callback.FutureEventCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class HelloController {

  @Autowired
  private EventPublisher eventPublisher;

  @RequestMapping(value = "/hello/{name}", method = RequestMethod.GET)
  public HelloResponse sayHi(@PathVariable String name) throws ExecutionException, InterruptedException {
    Future<HelloResponse> future = FutureEventCallback.forType(HelloResponse.class);
    eventPublisher.publish(new HelloCommand(name), future);

    HelloResponse helloResponse = future.get();

    return helloResponse;
  }
}
