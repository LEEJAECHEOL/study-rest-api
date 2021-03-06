package com.study.restapi.events;

import com.study.restapi.common.BaseControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@WebMvcTest // 단위 테스트
public class EventControllerTests extends BaseControllerTest {

  @Autowired
  private EventRepository eventRepository;


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
//      .andExpect(jsonPath("_links.self").exists())
//      .andExpect(jsonPath("_links.query-events").exists())
//      .andExpect(jsonPath("_links.update-event").exists())
      .andDo(document("create-event",
        links(
          linkWithRel("self").description("link to self"),
          linkWithRel("query-events").description("link to query events"),
          linkWithRel("update-event").description("link to update an existing event"),
          linkWithRel("profile").description("link to profile")
        ),
        requestHeaders(
          headerWithName(HttpHeaders.ACCEPT).description("accept header"),
          headerWithName(HttpHeaders.CONTENT_TYPE).description("content-type header")
        ),
        requestFields(
          fieldWithPath("name").description("Name of New Event"),
          fieldWithPath("description").description("Description of New Event"),
          fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of New Event"),
          fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of New Event"),
          fieldWithPath("beginEventDateTime").description("beginEventDateTime of New Event"),
          fieldWithPath("endEventDateTime").description("endEventDateTime of New Event"),
          fieldWithPath("location").description("location of New Event"),
          fieldWithPath("basePrice").description("basePrice of New Event"),
          fieldWithPath("maxPrice").description("maxPrice of New Event"),
          fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of New Event")
        ),
        responseHeaders(
          headerWithName(HttpHeaders.LOCATION).description("LOCATION header"),
          headerWithName(HttpHeaders.CONTENT_TYPE).description("content-type header")
        ),
        responseFields(
          // link를 명시했는데 에러가 발생함 (response에 _links 가 있기 때문) -> relaxedResponseFields 사용하면 됨.
          // 근데 다 명시하는 거를 추천함.
          fieldWithPath("id").description("identifier of New Event"),
          fieldWithPath("name").description("Name of New Event"),
          fieldWithPath("description").description("Description of New Event"),
          fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of New Event"),
          fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of New Event"),
          fieldWithPath("beginEventDateTime").description("beginEventDateTime of New Event"),
          fieldWithPath("endEventDateTime").description("endEventDateTime of New Event"),
          fieldWithPath("location").description("location of New Event"),
          fieldWithPath("basePrice").description("basePrice of New Event"),
          fieldWithPath("maxPrice").description("maxPrice of New Event"),
          fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of New Event"),
          fieldWithPath("free").description("free of New Event"),
          fieldWithPath("offline").description("offline of New Event"),
          fieldWithPath("eventStatus").description("eventStatus of New Event"),
          fieldWithPath("_links.self.href").description("link to self"),
          fieldWithPath("_links.query-events.href").description("link to query events"),
          fieldWithPath("_links.update-event.href").description("link to update an existing event"),
          fieldWithPath("_links.profile.href").description("link to profile")
        )
      ))
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
      .andExpect(jsonPath("errors[0].objectName").exists())
      .andExpect(jsonPath("errors[0].defaultMessage").exists())
      .andExpect(jsonPath("errors[0].code").exists())
      .andExpect(jsonPath("_links.index").exists())
    ;
  }

  /**
   * org.springframework.http.converter.HttpMessageNotWritableException
   */

  @Test
  @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
  public void queryEvents() throws Exception {
    IntStream.range(0, 30).forEach(this::generateEvent);

    mockMvc.perform(get("/api/events")
        .param("page", "1")
        .param("size", "10")
        .param("sort", "name,DESC")
      )
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("page").exists())
      .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
      .andExpect(jsonPath("_links.self").exists())
      .andExpect(jsonPath("_links.profile").exists())
      .andDo(document("query-events"))
    ;
  }

  private Event generateEvent(int index) {
    Event event = Event.builder()
      .name("Spring " + index)
      .description("Rest API Study")
      .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 14, 14, 21))
      .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 15, 14, 21))
      .beginEventDateTime(LocalDateTime.of(2018, 11, 14, 14, 21))
      .endEventDateTime(LocalDateTime.of(2018, 11, 16, 14, 23))
      .basePrice(100)
      .maxPrice(200)
      .limitOfEnrollment(100)
      .location("Seoul anyWhere")
      .free(false)
      .offline(true)
      .eventStatus(EventStatus.PUBLISHED)
      .build();

    return eventRepository.save(event);
  }


  @Test
  @DisplayName("기존의 이벤트를 하나 조회하기")
  public void getEvent() throws Exception {
    Event event = generateEvent(100);
    mockMvc.perform(get("/api/events/{id}", event.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("name").exists())
      .andExpect(jsonPath("id").exists())
      .andExpect(jsonPath("_links.self").exists())
      .andExpect(jsonPath("_links.profile").exists())
      .andDo(document("get-an-event"))
    ;
  }

  @Test
  @DisplayName("없는 이벤트를 조회했을 때 404 응답받기")
  public void getEvent404() throws Exception {
    mockMvc.perform(get("/api/events/111"))
      .andExpect(status().isNotFound())
    ;
  }


  @Test
  @DisplayName("이벤트를 정상적으로 수정하기")
  public void updateEvent() throws Exception {
    Event event = generateEvent(70);
    EventDto eventDto = modelMapper.map(event, EventDto.class);
    String eventName = "Updated Event";
    eventDto.setName(eventName);

    mockMvc.perform(put("/api/events/{id}", event.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(eventDto))
      )
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("name").value(eventName))
      .andExpect(jsonPath("_links.self").exists())
      .andDo(document("update-event"))
    ;
  }

  @Test
  @DisplayName("입력값이 비어있는 경우에 이벤트 수정 실패")
  public void updateEvent400_Empty() throws Exception {
    Event event = generateEvent(70);
    EventDto eventDto = new EventDto();

    mockMvc.perform(put("/api/events/{id}", event.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(eventDto))
      )
      .andDo(print())
      .andExpect(status().isBadRequest())
    ;
  }

  @Test
  @DisplayName("입력값이 잘못된 경우에 이벤트 수정 실패")
  public void updateEvent400_Wrong() throws Exception {
    Event event = generateEvent(70);
    EventDto eventDto = this.modelMapper.map(event, EventDto.class);
    eventDto.setBasePrice(20000);
    eventDto.setMaxPrice(1000);

    mockMvc.perform(put("/api/events/{id}", event.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(eventDto))
      )
      .andDo(print())
      .andExpect(status().isBadRequest())
    ;
  }
  @Test
  @DisplayName("존재하지 않는 이벤트 수정 실패")
  public void updateEvent404() throws Exception {
    Event event = generateEvent(70);
    EventDto eventDto = this.modelMapper.map(event, EventDto.class);

    mockMvc.perform(put("/api/events/333333333", event.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(eventDto))
      )
      .andDo(print())
      .andExpect(status().isNotFound())
    ;
  }

}
