package com.lucanet.packratcollector.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucanet.packratcommon.model.HealthCheckHeader;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class HealthCheckHeaderDeserializer implements Deserializer<HealthCheckHeader> {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  private final Logger logger;
  private final ObjectMapper objectMapper;

  // ============================  Constructors  ===========================79
  public HealthCheckHeaderDeserializer() {
    logger = LoggerFactory.getLogger(HealthCheckHeaderDeserializer.class);
    objectMapper = new ObjectMapper();
  }

  // ============================ Public Methods ===========================79
  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    //No-Op
  }

  @Override
  public HealthCheckHeader deserialize(String topic, byte[] data) {
    try {
      return objectMapper.readValue(data, HealthCheckHeader.class);
    } catch (IOException ioe) {
      logger.error("Error deserializing HealthCheckHeader: {}", ioe.getMessage());
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
