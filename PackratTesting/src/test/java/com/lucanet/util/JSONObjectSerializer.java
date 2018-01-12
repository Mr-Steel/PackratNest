package com.lucanet.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class JSONObjectSerializer implements Serializer<Object> {

  private final ObjectMapper objectMapper;

  public JSONObjectSerializer() {
    objectMapper = new ObjectMapper();
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    //No-Op
  }

  @Override
  public byte[] serialize(String topic, Object data) {
    try {
      return objectMapper.writeValueAsBytes(data);
    } catch (JsonProcessingException jpe) {
      System.err.println(String.format("Error serializing JSON object for '%s': %s", topic, jpe.getMessage()));
      return null;
    }
  }

  @Override
  public void close() {
    //No-Op
  }
}
