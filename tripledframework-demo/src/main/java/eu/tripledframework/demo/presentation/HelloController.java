/*
 * Copyright 2015 TripleD, DTI-Consulting.
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

package eu.tripledframework.demo.presentation;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.tripledframework.demo.application.HelloCommand;
import eu.tripledframework.demo.application.HelloResponse;
import eu.tripledframework.eventbus.domain.CommandDispatcher;

@RestController
public class HelloController {

  @Autowired
  private CommandDispatcher commandDispatcher;

  @RequestMapping(value = "/hello/{name}", method = RequestMethod.GET)
  public HelloResponse sayHi(@PathVariable String name) throws ExecutionException, InterruptedException {
    Future<HelloResponse> future = commandDispatcher.dispatch(new HelloCommand(name));

    return future.get();
  }
}
