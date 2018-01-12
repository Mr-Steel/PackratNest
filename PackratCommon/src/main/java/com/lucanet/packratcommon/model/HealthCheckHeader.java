package com.lucanet.packratcommon.model;

/**
 * DTO Class representing the metadata related to a {@link HealthCheckRecord}.
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
public class HealthCheckHeader {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  /**
   * The computer group to which the computer that produced the HealthCheck record belongs.
   * <p>
   * This is represented by a serial ID.
   */
  private String serialId;
  /**
   * The computer that produced the HealthCheck record.
   * <p>
   * This is represented by a UUID.
   */
  private String systemUUID;
  /**
   * The session during which the HealthCheck record was produced.
   * <p>
   * This is represented by a timestamp of seconds elapsed since the UNIX epoch.
   */
  private Long sessionTimestamp;
  /**
   * The specific time when the HealthCheck record was produced.
   * <p>
   * This is represented by a timestamp of seconds elapsed since the UNIX epoch.
   */
  private Long healthCheckTimestamp;

  // ============================  Constructors  ===========================79
  /**
   * Default constructor.
   */
  public HealthCheckHeader() {
  }

  // ============================ Public Methods ===========================79
  /**
   * Get serial ID.
   * @return {@link #serialId}
   */
  public String getSerialId() {
    return serialId;
  }

  /**
   * Set serial ID.
   * @param serialId The new {@link #serialId}.
   */
  public void setSerialId(String serialId) {
    this.serialId = serialId;
  }

  /**
   * Get system UUID.
   * @return {@link #systemUUID}
   */
  public String getSystemUUID() {
    return systemUUID;
  }

  /**
   * Set system UUID.
   * @param systemUUID The new {@link #systemUUID}.
   */
  public void setSystemUUID(String systemUUID) {
    this.systemUUID = systemUUID;
  }

  /**
   * Get session timestamp.
   * @return {@link #sessionTimestamp}
   */
  public Long getSessionTimestamp() {
    return sessionTimestamp;
  }

  /**
   * Set session timestamp.
   * @param sessionTimestamp The new {@link #sessionTimestamp}.
   */
  public void setSessionTimestamp(Long sessionTimestamp) {
    this.sessionTimestamp = sessionTimestamp;
  }

  /**
   * Get HealthCheck timestamp.
   * @return {@link #healthCheckTimestamp}
   */
  public Long getHealthCheckTimestamp() {
    return healthCheckTimestamp;
  }

  /**
   * Set the specific time when the HealthCheck record was produced.
   * @param healthCheckTimestamp The new {@link #healthCheckTimestamp}.
   */
  public void setHealthCheckTimestamp(Long healthCheckTimestamp) {
    this.healthCheckTimestamp = healthCheckTimestamp;
  }

  /**
   * Get the string representation of the HealthCheck record metadata - this serves as the unique identifier.
   * @return The string representation consisting of the {@link #systemUUID}, the {@link #sessionTimestamp}, and the {@link #healthCheckTimestamp}.
   */
  @Override
  public String toString() {
    return String.format("%s:%d@%d", systemUUID, sessionTimestamp, healthCheckTimestamp);
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}

