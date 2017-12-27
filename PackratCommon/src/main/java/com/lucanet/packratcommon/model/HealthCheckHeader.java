package com.lucanet.packratcommon.model;

public class HealthCheckHeader {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  private String serialID;
  private String systemUUID;
  private long   sessionTimestamp;
  private long   healthCheckTimestamp;

  // ============================  Constructors  ===========================79
  public HealthCheckHeader() {
  }

  // ============================ Public Methods ===========================79
  public String getSerialID() {
    return serialID;
  }

  public void setSerialID(String serialID) {
    this.serialID = serialID;
  }

  public String getSystemUUID() {
    return systemUUID;
  }

  public void setSystemUUID(String systemUUID) {
    this.systemUUID = systemUUID;
  }

  public long getSessionTimestamp() {
    return sessionTimestamp;
  }

  public void setSessionTimestamp(long sessionTimestamp) {
    this.sessionTimestamp = sessionTimestamp;
  }

  public long getHealthCheckTimestamp() {
    return healthCheckTimestamp;
  }

  public void setHealthCheckTimestamp(long healthCheckTimestamp) {
    this.healthCheckTimestamp = healthCheckTimestamp;
  }

  @Override
  public String toString() {
    return String.format("%s:%d@%d", systemUUID, sessionTimestamp, healthCheckTimestamp);
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}

