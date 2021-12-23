package com.study.restapi.index;

import com.study.restapi.common.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class IndexControllerTest extends BaseControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void test() throws Exception {
    mockMvc.perform(get("/api/"))
      .andExpect(status().isOk())
      .andDo(print())
      .andExpect(jsonPath("_links.events").exists());
  }

}
