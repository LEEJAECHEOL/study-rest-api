package com.study.restapi.events;


import org.assertj.core.api.Assertions;
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


  @Test
  public void testFree() {
    // Given
    Event event = Event.builder()
      .basePrice(0)
      .maxPrice(0)
      .build();

    // When
    event.update();

    // Then
    Assertions.assertThat(event.isFree()).isTrue();

    // Given
    event = Event.builder()
      .basePrice(100)
      .maxPrice(0)
      .build();

    // When
    event.update();

    // Then
    Assertions.assertThat(event.isFree()).isFalse();


    // Given
    event = Event.builder()
      .basePrice(0)
      .maxPrice(100)
      .build();

    // When
    event.update();

    // Then
    Assertions.assertThat(event.isFree()).isFalse();
  }

  @Test
  public void testOffline() {
    Event event = Event.builder()
      .location("location")
      .build();

    event.update();

    Assertions.assertThat(event.isFree()).isTrue();


    event = Event.builder()
      .build();

    event.update();

    Assertions.assertThat(event.isFree()).isTrue();
  }
}