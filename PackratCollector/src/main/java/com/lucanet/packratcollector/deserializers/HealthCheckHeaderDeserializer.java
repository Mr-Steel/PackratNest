package com.lucanet.packratcollector.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucanet.packratcommon.model.HealthCheckHeader;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
  /**
   * Validator for ensuring properly-formatted HealthCheckHeader instances
   */
  private final Validator validator;

  // ============================  Constructors  ===========================79
  /**
   * Deserializer constructor.
   */
  public HealthCheckHeaderDeserializer() {
    logger = LoggerFactory.getLogger(HealthCheckHeaderDeserializer.class);
    objectMapper = new ObjectMapper();
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    this.validator = validatorFactory.getValidator();
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
      HealthCheckHeader healthCheckHeader = objectMapper.readValue(data, HealthCheckHeader.class);
      Set<ConstraintViolation<HealthCheckHeader>> violations = validator.validate(healthCheckHeader);
      if (violations.isEmpty()) {
        return healthCheckHeader;
      } else {
        String violationsStr = violations.stream()
            .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
            .collect(Collectors.joining("; "));
        logger.error("Constraint violations for deserializing HealthCheckHeader: {}", violationsStr);
        return null;
      }
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
