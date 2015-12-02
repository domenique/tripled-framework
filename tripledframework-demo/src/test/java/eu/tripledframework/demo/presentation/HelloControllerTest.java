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

import eu.tripledframework.demo.CommandDispatcherDemoApplication;
import eu.tripledframework.demo.SaidHelloDomainEvent;
import eu.tripledframework.demo.infrastructure.InMemoryHelloEventStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CommandDispatcherDemoApplication.class)
@WebAppConfiguration
public class HelloControllerTest {

  @Autowired
  private WebApplicationContext webApplicationContext;
  @Autowired
  private InMemoryHelloEventStore eventStore;
  private MockMvc mvc;

  @Before
  public void setUpMockMvc() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @After
  public void clearEvents() throws Exception {
    ReflectionTestUtils.setField(eventStore, "events", new ArrayList<Object>());
  }

  @Test
  public void whenGivenAValidName_shouldSayHi() throws Exception {
    // given
    String name = "John Doe";

    // when
    ResultActions result = sayHiTo(name);

    // then
    result.andExpect(status().is2xxSuccessful());
    result.andExpect(jsonPath("$.message", equalTo("Hello " + name)));
    List<Object> events = (List<Object>) ReflectionTestUtils.getField(eventStore, "events");
    assertThat(events, hasSize(1));
    assertThat(events.get(0), instanceOf(SaidHelloDomainEvent.class));
    assertThat(((SaidHelloDomainEvent)events.get(0)).getName(), equalTo(name));
  }

  @Test
  public void whenGivenAnInValidName_shouldBarf() throws Exception {
    // given
    String name = "dj";

    // when
    ResultActions result = sayHiTo(name);

    // then
    result.andDo(print());
    result.andExpect(status().is4xxClientError());
    result.andExpect(jsonPath("$.[0].message", equalTo("size must be between 3 and 2147483647")));
  }

  @Test
  public void whenSayingHiToTheDevil_shouldBarf() throws Exception {
    // given
    String name = "The devil";

    // when
    ResultActions result = sayHiTo(name);

    // then
    result.andDo(print());
    result.andExpect(status().is4xxClientError());
    result.andExpect(jsonPath("$.[0].message", equalTo("The execution failed with an uncaught exception.")));
  }

  private ResultActions sayHiTo(String name) throws Exception {
    return mvc.perform(get("/hello/{name}", name).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));
  }


}