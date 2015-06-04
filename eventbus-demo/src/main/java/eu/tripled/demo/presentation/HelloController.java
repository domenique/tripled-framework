package eu.tripled.demo.presentation;

import eu.tripled.eventbus.EventPublisher;
import eu.tripled.eventbus.callback.FutureEventCallback;
import eu.tripled.demo.application.HelloCommand;
import eu.tripled.demo.application.HelloResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class HelloController {

  @Autowired
  private EventPublisher eventPublisher;

  @RequestMapping(value = "/hello/{name}", method = RequestMethod.GET)
  public HelloResponse sayHi(String name) throws ExecutionException, InterruptedException {
    FutureEventCallback<HelloResponse> future = new FutureEventCallback<>();
    eventPublisher.publish(new HelloCommand(name), future);

    return future.get();

  }

}
