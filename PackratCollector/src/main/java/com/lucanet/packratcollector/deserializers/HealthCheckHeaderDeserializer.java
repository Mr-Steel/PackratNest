package com.lucanet.packratcollector.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucanet.packratcommon.model.HealthCheckHeader;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Implementation of {@link Deserializer} for processing HealthCheck message header data.
 * @see Deserializer
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
public class HealthCheckHeaderDeserializer implements Deserializer<HealthCheckHeader> {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  /**
   * Logger for the HealthCheckHeaderDeserializer instance.
   */
  private final Logger logger;
  /**
   * Mapper that will translate raw byte data to a {@link HealthCheckHeader}.
   */
  private final ObjectMapper objectMapper;

  // ============================  Constructors  ===========================79
  /**
   * Deserializer constructor.
   */
  public HealthCheckHeaderDeserializer() {
    logger = LoggerFactory.getLogger(HealthCheckHeaderDeserializer.class);
    objectMapper = new ObjectMapper();
  }

  // ============================ Public Methods ===========================79
  /**
   * Configure the deserializer. Currently, this method is not used.
   * @param configs Configs in key/value pairs.
   * @param isKey Whether is for key or value.
   */
  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    //No-Op
  }

  /**
   * Deserialize the raw byte data into a {@link HealthCheckHeader}.
   * @param topic The topic that the raw HealthCheck header data belongs to.
   * @param data The raw HealthCheck header data.
   * @return The HealthCheck header data represented by a {@link HealthCheckHeader}.
   */
  @Override
  public HealthCheckHeader deserialize(String topic, byte[] data) {
    try {
      return objectMapper.readValue(data, HealthCheckHeader.class);
    } catch (IOException ioe) {
      logger.error("Error deserializing HealthCheckHeader: {}", ioe.getMessage());
      return null;
    }
  }

  /**
   * Shut down the deserializer. Currently, this method is not used.
   */
  @Override
  public void close() {
    //No-Op
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
