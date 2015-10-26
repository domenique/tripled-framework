# TripleD [![build status](https://travis-ci.org/domenique/tripled-framework.svg?branch=master)](https://travis-ci.org/domenique/tripled-framework) [![Coverage Status](https://coveralls.io/repos/domenique/tripled-framework/badge.svg?branch=master)](https://coveralls.io/r/domenique/tripled-framework?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/eu.tripled-framework/spring-boot-eventbus-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/eu.tripled-framework/spring-boot-eventbus-starter)
A very opinionated framework to build applications using CQRS, event sourcing and domain driven design.

## Goal
The TripleD framework aims to take away as much boiler plate code as possible when building CQRS based applications which want to take advantage of event sourcing and domain driven design.

This framework aims to facilitate the creation and execution of commands. The idea is to be able to dispatch commands so that they can be executed in an (a)synchronous fashion.

## Usage
The framework supports Spring boot. This implies that if you add this framework jar to your classpath, it will be auto-configured with sensible defaults.

after adding the required dependencies, an `EventPublisher` and `EventSubscriber`  should be available in your application context.
Alternatively, When annotatating an `@Configuration` class with `@EnableEventHandlerSupport` you should be able to annotate any spring service with `@EventHandler` and it will be registered automatically to the `EventBus`.

To get started with the EventBus the following dependencies should be added to your gradle configuration
```groovy
dependencies {
    compile ("eu.tripledframework:spring-boot-eventbus-starter:0.0.1-SNAPSHOT")
 }
```


The following configuration annotations should be used if you which to automatically register spring services as eventHandlers.
```java
@EnableEventHandlerSupport(basePackage = "eu.tripledframework.demo")
public class EventBusDemoApplication {
    ....
}
```

The below sample illustrates how a springMVC controller would typically fire a command.
```java
@RestController
public class HelloController {

  @Autowired
  private EventPublisher eventPublisher;

  @RequestMapping(value = "/hello/{name}", method = RequestMethod.GET)
    public HelloResponse sayHi(@PathVariable String name) throws ExecutionException, InterruptedException {
      Future<HelloResponse> future = FutureEventCallback.forType(HelloResponse.class);
      eventPublisher.publish(new HelloCommand(name), future);

      return future.get();
    }
}
```

A commandHandler would then be implemented as following
```java
@EventHandler
public class HelloCommandHandler {

  @Handles(HelloCommand.class)
  public HelloResponse handleHelloCommand(HelloCommand helloCommand) {
    return new HelloResponse("Hello " + helloCommand.getName());
  }
}
```


> See the demo application which is incorporated in this repository as a sub project.

## Contribute
The project is still in a very early stage, however if you feel like contributing or have some brilliant ideas how to make this a killer framework, just contact me! I'm open for suggestions!
