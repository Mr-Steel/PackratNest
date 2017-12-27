package com.lucanet.packratcollector.deserializers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JSONDeserializer implements Deserializer<Map<String, Object>> {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  private final Logger logger;
  private final ObjectMapper objectMapper;
  private final TypeReference<HashMap<String, Object>> typeReference;
  private final StringDeserializer stringDeserializer;

  // ============================  Constructors  ===========================79
  public JSONDeserializer() {
    logger = LoggerFactory.getLogger(JSONDeserializer.class);
    objectMapper = new ObjectMapper();
    typeReference = new TypeReference<HashMap<String, Object>>(){};
    stringDeserializer = new StringDeserializer();
  }

  // ============================ Public Methods ===========================79
  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    stringDeserializer.configure(configs, isKey);
  }

  @Override
  public Map<String, Object> deserialize(String topic, byte[] data) {
    try {
      return objectMapper.readValue(data, typeReference);
    } catch (Exception e) {
      logger.error("Error parsing value for '{}' message: {}", topic, e.getMessage());
      return null;
    }
  }

  @Override
  public void close() {
    stringDeserializer.close();
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
