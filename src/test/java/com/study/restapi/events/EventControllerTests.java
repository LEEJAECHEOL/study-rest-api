package com.study.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
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
  @DisplayName("이벤트를 생성하는 테스트")
  public void createEvent() throws Exception {
    EventDto event = EventDto.builder()
      .name("Spring")
      .description("Rest API Study")
      .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 14, 14, 21))
      .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 15, 14, 21))
      .beginEventDateTime(LocalDateTime.of(2018, 11, 14, 14, 21))
      .endEventDateTime(LocalDateTime.of(2018, 11, 16, 14, 23))
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
      .andExpect(jsonPath("free").value(false))
      .andExpect(jsonPath("offline").value(true))
      .andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.DRAFT.name())))
      .andExpect(header().exists(HttpHeaders.LOCATION))
      .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
      .andExpect(jsonPath("_links.self").exists())
      .andExpect(jsonPath("_links.query-events").exists())
      .andExpect(jsonPath("_links.update-event").exists())
//      .andExpect(jsonPath("_link.profile").exists())
    ;
  }

  @Test
  @DisplayName("입력 받을 수 없는 값이 있는 경우 에러가 발생하는 테스트")
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
  @DisplayName("입력값이 비어있는 경우 에러가 발생하는 테스트")
  public void createEvent_Bad_Request_Empty_Input() throws Exception {
    EventDto eventDto = EventDto.builder().build();

    mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(eventDto)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("입력 값이 잘못된 경우 발생하는 에러가 발생하는 테스트")
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
      .andExpect(status().isBadRequest())
      .andDo(print())
      .andExpect(jsonPath("$[0].objectName").exists())
      .andExpect(jsonPath("$[0].defaultMessage").exists())
      .andExpect(jsonPath("$[0].code").exists())
    ;
  }

}
