package com.lucanet.packratcollector.deserializers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileLinesDeserializer implements Deserializer<List<String>> {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  private final Logger logger;
  private final ObjectMapper objectMapper;
  private final TypeReference<ArrayList<String>> typeReference;

  // ============================  Constructors  ===========================79
  public FileLinesDeserializer() {
    logger = LoggerFactory.getLogger(FileLinesDeserializer.class);
    objectMapper = new ObjectMapper();
    typeReference = new TypeReference<ArrayList<String>>() {};
  }

  // ============================ Public Methods ===========================79
  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    //No-Op
  }

  @Override
  public List<String> deserialize(String topic, byte[] data) {
    try {
      return objectMapper.readValue(data, typeReference);
    } catch (Exception e) {
      logger.error("Error parsing value for '{}' message: {}", topic, e.getMessage());
      return null;
    }
  }

  @Override
  public void close() {
    //No-Op
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
