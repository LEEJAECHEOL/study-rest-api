package com.study.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@WebMvcTest // 단위 테스트
@SpringBootTest // 통합테스트
@AutoConfigureMockMvc
public class EventControllerTests {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

//  @MockBean
//  private EventRepository eventRepository;

  @Test
  public void createEvent() throws Exception {
    EventDto event = EventDto.builder()
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

//    Mockito.when(eventRepository.save(event)).thenReturn(event);

    mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaTypes.HAL_JSON)
        .content(objectMapper.writeValueAsString(event))
      )
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(jsonPath("id").exists())
      .andExpect(jsonPath("id").value(Matchers.not(100)))
      .andExpect(jsonPath("free").value(Matchers.not(true)))
      .andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.DRAFT.name())))
      .andExpect(header().exists(HttpHeaders.LOCATION))
      .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE));
  }

  @Test
  public void createEvent_Bad_Request() throws Exception {
    Event event = Event.builder()
      .id(10)
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
      .free(true)
      .offline(false)
      .eventStatus(EventStatus.PUBLISHED)
      .build();

    mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaTypes.HAL_JSON)
        .content(objectMapper.writeValueAsString(event)))
      .andDo(print())
      .andExpect(status().isBadRequest())
    ;
  }

  @Test
  public void createEvent_Bad_Request_Empty_Input() throws Exception {
    EventDto eventDto = EventDto.builder().build();

    mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(eventDto)))
      .andExpect(status().isBadRequest());
  }

  @Test
  public void createEvent_Bad_Request_Wrong_Input() throws Exception {
    EventDto eventDto = EventDto.builder()
      .name("Spring")
      .description("Rest API Study")
      .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 14, 14, 21))
      .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 15, 14, 21))
      .beginEventDateTime(LocalDateTime.of(2018, 11, 15, 14, 21))
      .endEventDateTime(LocalDateTime.of(2018, 11, 14, 14, 21))
      .basePrice(10000)
      .maxPrice(200)
      .limitOfEnrollment(100)
      .location("Seoul anyWhere")
      .build();

    mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(eventDto)))
      .andExpect(status().isBadRequest());
  }
}
