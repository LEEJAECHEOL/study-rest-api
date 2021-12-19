package com.study.restapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {
  public void validate(EventDto eventDto, Errors errors) {
    if (eventDto.getMaxPrice() < eventDto.getBasePrice() && eventDto.getMaxPrice() > 0) {
      errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong"); // field error
      errors.rejectValue("maxPrice", "wrongValue", "maxPrice is wrong");
      errors.reject("wrongPrices", "Values of Prices are wrong"); // global error
    }

    LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
    if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
    endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
    endEventDateTime.isBefore(eventDto.getBeginEventDateTime())) {
      errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
    }

    // TODO beginEventDateTime
    // TODO CloseEnrollmentDateTime
  }
}
