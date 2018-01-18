package com.lucanet.packratcollector.deserializers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link Deserializer} for processing HealthCheck message file data
 * @see Deserializer
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
public class FileLinesDeserializer implements Deserializer<List<String>> {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  /**
   * Logger for the FileLinesDeserializer instance.
   */
  private final Logger logger;
  /**
   * Mapper that will translate raw byte data to a list of strings.
   */
  private final ObjectMapper objectMapper;
  /**
   * Type reference for the {@link #objectMapper} to translate the raw byte data.
   */
  private final TypeReference<ArrayList<String>> typeReference;

  // ============================  Constructors  ===========================79
  /**
   * Deserializer constructor.
   */
  public FileLinesDeserializer() {
    logger = LoggerFactory.getLogger(FileLinesDeserializer.class);
    objectMapper = new ObjectMapper();
    typeReference = new TypeReference<ArrayList<String>>() {};
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
   * Deserialize the raw byte data into a list of strings.
   * @param topic The topic that the raw HealthCheck data belongs to.
   * @param data The raw HealthCheck data.
   * @return The file data represented by a list of strings.
   */
  @Override
  public List<String> deserialize(String topic, byte[] data) {
    List<String> deserializedList = null;
    if ((data != null) && (data.length > 0)) { //Only attempt to deserialize if data is a non-empty byte array
      try {
        List<String> fileLines = objectMapper.readValue(data, typeReference);
        if (!fileLines.isEmpty()) {
          deserializedList = fileLines;
        }
      } catch (Exception e) {
        logger.error(String.format("Error parsing value for '%s':", topic), e);
      }
    }
    return deserializedList;
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
