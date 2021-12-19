//package com.study.restapi.events;
//
//import com.fasterxml.jackson.annotation.JsonUnwrapped;
//import org.springframework.hateoas.RepresentationModel;
//
//public class EventResource extends RepresentationModel<EventResource> {
//
//  @JsonUnwrapped
//  private Event event;
//
//  public EventResource(Event event) {
//    this.event = event;
//  }
//
//  public Event getEvent() {
//    return event;
//  }
//}
package com.study.restapi.events;

import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {

  public EventResource(Event event) {
    super(event);
    add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
  }
}
