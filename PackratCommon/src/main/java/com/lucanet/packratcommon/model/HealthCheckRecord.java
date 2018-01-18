package com.lucanet.packratcommon.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.bson.Document;

/**
 * Wrapper DTO class representing a HealthCheck entity for the MongoDB implementation.
 * @see Document
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
public class HealthCheckRecord<T> extends Document {
  // =========================== Class Variables ===========================79
  /**
   * "serialId" key for usage in the MongoDB database.
   * @see HealthCheckHeader#serialId
   */
  public static final String SERIAL_ID = "serialId";
  /**
   * "systemUUID" key for usage in the MongoDB database.
   * @see HealthCheckHeader#systemUUID
   */
  public static final String SYSTEM_UUID = "systemUUID";
  /**
   * "sessionTimestamp" key for usage in the MongoDB database.
   * @see HealthCheckHeader#sessionTimestamp
   */
  public static final String SESSION_TIMESTAMP = "sessionTimestamp";
  /**
   * "healthCheckTimestamp" key for usage in the MongoDB database.
   * @see HealthCheckHeader#healthCheckTimestamp
   */
  public static final String HEALTHCHECK_TIMESTAMP = "healthCheckTimestamp";
  /**
   * "version" key for usage in the MongoDB database.
   * @see HealthCheckHeader#version
   */
  public static final String VERSION = "version";
  /**
   * "data" key for usage in the MongoDB database.
   */
  public static final String DATA = "data";

  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  // ============================  Constructors  ===========================79
  /**
   * Constructor for the HealthCheck record.
   * @param healthCheckHeader The metadata for the HealthCheck record.
   * @param data The HealthCheck data.
   * @see HealthCheckHeader
   */
  public HealthCheckRecord(HealthCheckHeader healthCheckHeader, T data) {
    put("_id", healthCheckHeader.getUniqueId());
    put(SERIAL_ID, healthCheckHeader.getSerialId());
    put(SYSTEM_UUID, healthCheckHeader.getSystemUUID());
    put(SESSION_TIMESTAMP, healthCheckHeader.getSessionTimestamp());
    put(HEALTHCHECK_TIMESTAMP, healthCheckHeader.getHealthCheckTimestamp());
    put(VERSION, healthCheckHeader.getVersion());
    put(DATA, data);
  }

  // ============================ Public Methods ===========================79
  /**
   * Get serial ID.
   * @return The computer group to which the computer that produced the HealthCheck record belongs.
   * @see HealthCheckHeader#serialId
   */
  public String getSerialId() {
    return getString(SERIAL_ID);
  }

  /**
   * Set serialID.
   * @param serialId The new serial ID.
   * @see HealthCheckHeader#serialId
   */
  public void setSerialId(String serialId) {
    put(SERIAL_ID, serialId);
  }

  /**
   * Get system UUID.
   * @return The computer that produced the HealthCheck record.
   * @see HealthCheckHeader#systemUUID
   */
  public String getSystemUUID() {
    return getString(SYSTEM_UUID);
  }

  /**
   * Set system UUID.
   * @param systemUUID The new system UUID.
   * @see HealthCheckHeader#systemUUID
   */
  public void setSystemUUID(String systemUUID) {
    put(SYSTEM_UUID, systemUUID);
  }

  /**
   * Get session timestamp.
   * @return The session during which the HealthCheck record was produced.
   * @see HealthCheckHeader#sessionTimestamp
   */
  public long getSessionTimestamp() {
    return getLong(SESSION_TIMESTAMP);
  }

  /**
   * Set session timestamp.
   * @param sessionTimestamp The new session timestamp.
   * @see HealthCheckHeader#sessionTimestamp
   */
  public void setSessionTimestamp(long sessionTimestamp) {
    put(SESSION_TIMESTAMP, sessionTimestamp);
  }

  /**
   * Get HealthCheck timestamp.
   * @return The specific time when the HealthCheck record was produced.
   * @see HealthCheckHeader#healthCheckTimestamp
   */
  public long getHealthCheckTimestamp() {
    return getLong(HEALTHCHECK_TIMESTAMP);
  }

  /**
   * Set HealthCheck timestamp.
   * @param healthCheckTimestamp The new HealthCheck timestamp.
   * @see HealthCheckHeader#healthCheckTimestamp
   */
  public void setHealthCheckTimestamp(long healthCheckTimestamp) {
    put(HEALTHCHECK_TIMESTAMP, healthCheckTimestamp);
  }

  /**
   * Get HealthCheck data.
   * @return The HealthCheck data.
   */
  @SuppressWarnings("unchecked")
  public T getData() {
    return (T) get(DATA);
  }

  /**
   * Set HealthCheck data.
   * @param data The new HealthCheck data.
   */
  public void setData(JsonNode data) {
    put(DATA, data);
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
