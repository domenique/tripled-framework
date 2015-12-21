/*
 * Copyright 2015 TripleD framework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.tripledframework.demo.application;

import eu.tripledframework.demo.SaidHelloDomainEvent;
import eu.tripledframework.eventbus.domain.CommandDispatcher;
import eu.tripledframework.eventbus.domain.annotation.EventHandler;
import eu.tripledframework.eventbus.domain.annotation.Handles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@EventHandler
@Component
public class HelloCommandHandler {

  @Autowired
  private CommandDispatcher eventPublisher;

  @Handles(HelloCommand.class)
  public HelloResponse handleHelloCommand(HelloCommand helloCommand) {
    if (helloCommand.getName().equals("The devil")) {
      throw new IllegalArgumentException("I'm not saying hi to the devil! :P");
    }
    HelloResponse helloResponse = new HelloResponse("Hello " + helloCommand.getName());

    eventPublisher.dispatch(new SaidHelloDomainEvent(helloCommand.getName()));

    return helloResponse;
  }

}
