package com.lucanet.packratcommon.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.bson.Document;

public class HealthCheckRecord<T> extends Document {
  // =========================== Class Variables ===========================79
  public static final String SERIAL_ID             = "serialID";
  public static final String SYSTEM_UUID           = "systemUUID";
  public static final String SESSION_TIMESTAMP     = "sessionTimestamp";
  public static final String HEALTHCHECK_TIMESTAMP = "healthCheckTimestamp";
  public static final String DATA                  = "data";

  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  // ============================  Constructors  ===========================79
  public HealthCheckRecord(HealthCheckHeader healthCheckHeader, T data) {
    put("_id", healthCheckHeader.toString());
    put(SERIAL_ID, healthCheckHeader.getSerialID());
    put(SYSTEM_UUID, healthCheckHeader.getSystemUUID());
    put(SESSION_TIMESTAMP, healthCheckHeader.getSessionTimestamp());
    put(HEALTHCHECK_TIMESTAMP, healthCheckHeader.getHealthCheckTimestamp());
    put(DATA, data);
  }

  // ============================ Public Methods ===========================79
  public String getSerialID() {
    return getString(SERIAL_ID);
  }

  public void setSystemOwner(String systemOwner) {
    put(SERIAL_ID, systemOwner);
  }

  public String getSystemUUID() {
    return getString(SYSTEM_UUID);
  }

  public void setSystemUUID(String systemUUID) {
    put(SYSTEM_UUID, systemUUID);
  }

  public long getSessionTimestamp() {
    return getLong(SESSION_TIMESTAMP);
  }

  public void setSessionTimestamp(long sessionTimestamp) {
    put(SESSION_TIMESTAMP, sessionTimestamp);
  }

  public long getHealthCheckTimestamp() {
    return getLong(HEALTHCHECK_TIMESTAMP);
  }

  public void setHealthCheckTimestamp(long healthCheckTimestamp) {
    put(HEALTHCHECK_TIMESTAMP, healthCheckTimestamp);
  }

  @SuppressWarnings("unchecked")
  public T getData() {
    return (T) get(DATA);
  }

  public void setData(JsonNode data) {
    put(DATA, data);
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
