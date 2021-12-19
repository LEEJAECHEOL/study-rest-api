package com.study.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@SpringBootTest
@WebMvcTest
public class EventControllerTests {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private EventRepository eventRepository;

  @Test
  public void createEvent() throws Exception {
    Event event = Event.builder()
      .name("Spring")
      .description("Rest API Study")
      .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 14, 14, 21))
      .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 15, 14, 21))
      .beginEventDateTime(LocalDateTime.of(2018, 11, 15, 14, 21))
      .endEventDateTime(LocalDateTime.of(2018, 11, 16, 14, 21))
      .basePrice(100)
      .maxPrice(200)
      .limitOfEnrollment(100)
      .location("Seoul anyWhere")
      .build();
    event.setId(1);

    Mockito.when(eventRepository.save(event)).thenReturn(event);

    mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaTypes.HAL_JSON)
        .content(objectMapper.writeValueAsString(event))
      )
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("id").exists());
  }

}
