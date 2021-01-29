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
package eu.tripledframework.demo.presentation;

import eu.tripledframework.demo.CommandDispatcherDemoApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CommandDispatcherDemoApplication.class)
@WebAppConfiguration
public class HelloControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mvc;

    @BeforeEach
    public void setUpMockMvc() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
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

    @Test
    public void whenSayingHiWithoutName_shouldReturnWithUsername() throws Exception {

        // when
        ResultActions result = sayHi();

        // then
        result.andExpect(status().is2xxSuccessful());
        result.andExpect(jsonPath("$.message", equalTo("Hello authenticated user testuser")));
    }

    private ResultActions sayHiTo(String name) throws Exception {
        return mvc
                .perform(get("/hello/{name}", name)
                        .with(httpBasic("testuser", "password"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions sayHi() throws Exception {
        return mvc
                .perform(get("/hello")
                        .with(httpBasic("testuser", "password"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON));
    }


}