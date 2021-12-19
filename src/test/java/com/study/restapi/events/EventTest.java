package com.study.restapi.events;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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


//  @Test
  @ParameterizedTest(name = "{index} => basePrice={0}, maxPrice={1}, isFree={2}")
  @CsvSource({
    "0, 0, true",
    "100, 0, false",
    "0, 100, false"
  })
  public void testFree(int basePrice, int maxPrice, boolean isFree) {
    // Given
    Event event = Event.builder()
      .basePrice(basePrice)
      .maxPrice(maxPrice)
      .build();

    // When
    event.update();

    // Then
    Assertions.assertThat(event.isFree()).isEqualTo(isFree);
  }

//  @Test
  @ParameterizedTest
  @MethodSource("parametersForTestOffline")
  public void testOffline(String location, boolean isOffline) {
    Event event = Event.builder()
      .location(location)
      .build();

    event.update();

    Assertions.assertThat(event.isOffline()).isEqualTo(isOffline);
  }

  private static Stream<Arguments> parametersForTestOffline() {
    return Stream.of(
      Arguments.of("location1", true),
      Arguments.of(null, false),
      Arguments.of(" ", true)
    );
  }
}