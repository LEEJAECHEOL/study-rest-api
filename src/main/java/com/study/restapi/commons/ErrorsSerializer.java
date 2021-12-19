package com.study.restapi.commons;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {

  @Override
  public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartArray();
    errors.getFieldErrors().stream().forEach(fieldError -> {
      try {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("field", fieldError.getField());
        jsonGenerator.writeStringField("objectName", fieldError.getObjectName());
        jsonGenerator.writeStringField("code", fieldError.getCode());
        jsonGenerator.writeStringField("defaultMessage", fieldError.getDefaultMessage());
        Object rejectedValue = fieldError.getRejectedValue();
        if (rejectedValue != null) {
          jsonGenerator.writeStringField("rejectedValue", rejectedValue.toString());
        }
        jsonGenerator.writeEndObject();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    errors.getGlobalErrors().stream().forEach(objectError -> {
      try {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("objectName", objectError.getObjectName());
        jsonGenerator.writeStringField("code", objectError.getCode());
        jsonGenerator.writeStringField("defaultMessage", objectError.getDefaultMessage());
        jsonGenerator.writeEndObject();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    jsonGenerator.writeEndArray();
  }
}
