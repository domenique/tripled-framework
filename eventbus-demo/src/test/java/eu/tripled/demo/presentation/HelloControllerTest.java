package eu.tripled.demo.presentation;

import eu.tripled.demo.CommandDispatcherDemoApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
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
  private MockMvc mvc;

  @Before
  public void setUp() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
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

  private ResultActions sayHiTo(String name) throws Exception {
    return mvc.perform(get("/hello/{name}", name).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));
  }
}