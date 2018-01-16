package com.lucanet.packratcollector.deserializers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link Deserializer} for processing HealthCheck message JSON data.
 * @see Deserializer
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
public class JSONDeserializer implements Deserializer<Map<String, Object>> {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  /**
   * Logger for the JSONDeserializer instance.
   */
  private final Logger logger;
  /**
   * Mapper that will translate raw byte data to a map of objects.
   */
  private final ObjectMapper objectMapper;
  /**
   * Type reference for the {@link #objectMapper} to translate the raw byte data.
   */
  private final TypeReference<HashMap<String, Object>> typeReference;

  // ============================  Constructors  ===========================79
  /**
   * Deserializer constructor.
   */
  public JSONDeserializer() {
    logger = LoggerFactory.getLogger(JSONDeserializer.class);
    objectMapper = new ObjectMapper();
    typeReference = new TypeReference<HashMap<String, Object>>() { };
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
   * Deserialize the raw byte data into a map of objects.
   * @param topic The topic that the raw HealthCheck data belongs to.
   * @param data The raw HealthCheck data.
   * @return The JSON data represented by a map of objects.
   */
  @Override
  public Map<String, Object> deserialize(String topic, byte[] data) {
    try {
      JsonNode deserializedData = objectMapper.readTree(data);
      if (!deserializedData.isNull()) {
        //Convert to a POJO HashMap, as MongoDB cannot read JsonNode entities properly
        return objectMapper.convertValue(deserializedData, typeReference);
      } else {
        logger.warn("No data parsed for '{}'", topic);
        return null;
      }
    } catch (Exception e) {
      logger.error(String.format("Error parsing value for '%s':", topic), e);
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
