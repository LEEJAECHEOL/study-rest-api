package com.study.restapi.events;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// cmd shift t : 테스트 파일 생성
class EventTest {

  @Test
  public void builder() {
    Event event = Event.builder()
      .name("Event Tile")
      .description("description")
      .build();
    assertThat(event).isNotNull();
  }

  @Test
  public void javaBean() {
    Event event = new Event();
    String title = "Test Title";
    event.setName(title);
    String description = "Test Description";
    event.setDescription(description);

    assertThat(event.getName()).isEqualTo(title);
    assertThat(event.getDescription()).isEqualTo(description);
  }
}