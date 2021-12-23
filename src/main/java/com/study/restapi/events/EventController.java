package com.study.restapi.events;

import com.study.restapi.commons.ErrorsResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {

  private final EventRepository eventRepository;
  private final ModelMapper modelMapper;
  private final EventValidator eventValidator;

  @PostMapping
  public ResponseEntity creatEvent(@Valid @RequestBody EventDto eventDto, Errors errors) {
    if (errors.hasErrors()) {
      return badRequest(errors);
    }
    eventValidator.validate(eventDto, errors);
    if (errors.hasErrors()) {
      return badRequest(errors);
    }

    Event event = modelMapper.map(eventDto, Event.class);
    event.update();
    Event newEvent = eventRepository.save(event);
    // import 참고 !!
    WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
    URI createdUri = selfLinkBuilder.toUri();

    EventResource eventResource = new EventResource(event);
    eventResource.add(linkTo(EventController.class).withRel("query-events"));
//    eventResource.add(selfLinkBuilder.withSelfRel());
    eventResource.add(selfLinkBuilder.withRel("update-event"));
    eventResource.add(Link.of("/docs/index.html#resources-events-create", "profile"));
//    eventResource.add(new Link("/docs/index.html#resources-events-create", LinkRelation.of("profile")));

    return ResponseEntity.created(createdUri).body(eventResource);
  }

  private ResponseEntity badRequest(Errors errors) {
    return ResponseEntity.badRequest().body(new ErrorsResource(errors));
  }

  @GetMapping
  public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
    Page<Event> page = eventRepository.findAll(pageable);
    PagedModel<EntityModel<Event>> resources = assembler.toModel(page, entity -> new EventResource(entity));
    resources.add(Link.of("/docs/index.html#resources-events-list", "profile"));

    return ResponseEntity.ok(resources);
  }

  @GetMapping("/{id}")
  public ResponseEntity getEvent(@PathVariable Integer id) {
    Optional<Event> optionalEvent = eventRepository.findById(id);
    if (!optionalEvent.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    Event event = optionalEvent.get();
    EventResource eventResource = new EventResource(event);
    eventResource.add(Link.of("/docs/index.html#resources-events-get", "profile"));

    return ResponseEntity.ok(eventResource);
  }

  @PutMapping("/{id}")
  public ResponseEntity updateEvent(@PathVariable Integer id, @Valid @RequestBody EventDto eventDto, Errors errors) {
    Optional<Event> optionalEvent = eventRepository.findById(id);
    if (!optionalEvent.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    if (errors.hasErrors()) {
      return badRequest(errors);
    }
    eventValidator.validate(eventDto, errors);
    if (errors.hasErrors()) {
      return badRequest(errors);
    }

    Event existEvent = optionalEvent.get();
    modelMapper.map(eventDto, existEvent);
    Event updatedEvent = eventRepository.save(existEvent);
    EventResource eventResource = new EventResource(updatedEvent);
    eventResource.add(Link.of("/docs/index.html#resources-events-update", "profile"));

    return ResponseEntity.ok(eventResource);
  }

}
