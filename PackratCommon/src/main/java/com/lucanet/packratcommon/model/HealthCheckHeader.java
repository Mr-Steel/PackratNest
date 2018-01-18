package com.lucanet.packratcommon.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.beans.Transient;
import java.io.Serializable;

/**
 * DTO Class representing the metadata related to a {@link HealthCheckRecord}.
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
public class HealthCheckHeader implements Serializable {
  // =========================== Class Variables ===========================79
  private static final long serialVersionUID = 4714029877151630379L;
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  /**
   * The computer group to which the computer that produced the HealthCheck record belongs.
   * <p>
   * This is represented by a serial ID.
   */
  @NotNull
  @NotEmpty
  private String serialId;
  /**
   * The computer that produced the HealthCheck record.
   * <p>
   * This is represented by a UUID.
   */
  @NotNull
  @NotEmpty
  private String systemUUID;
  /**
   * The session during which the HealthCheck record was produced.
   * <p>
   * This is represented by a timestamp of seconds elapsed since the UNIX epoch.
   */
  @NotNull
  private Long sessionTimestamp;
  /**
   * The specific time when the HealthCheck record was produced.
   * <p>
   * This is represented by a timestamp of seconds elapsed since the UNIX epoch.
   */
  @NotNull
  private Long healthCheckTimestamp;
  /**
   * Version of the HealthCheck record.
   */
  @NotNull
  @Min(1)
  private Integer version;

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
   * Get HealthCheck record version.
   * @return {@link #version}
   */
  public Integer getVersion() {
    return version;
  }
  /**
   * Set the HealthCheck record version.
   * @param version The new {@link #version}.
   */
  public void setVersion(Integer version) {
    this.version = version;
  }

  /**
   * Get the string representation of the HealthCheck record metadata - this serves as the unique identifier.
   * @return The string representation consisting of the {@link #systemUUID}, the {@link #sessionTimestamp}, and the {@link #healthCheckTimestamp}.
   */
  @Transient
  public String getUniqueId() {
    return String.format("%s:%d@%d", systemUUID, sessionTimestamp, healthCheckTimestamp);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HealthCheckHeader that = (HealthCheckHeader) o;

    if (getSerialId() != null ? !getSerialId().equals(that.getSerialId()) : that.getSerialId() != null) return false;
    if (getSystemUUID() != null ? !getSystemUUID().equals(that.getSystemUUID()) : that.getSystemUUID() != null)
      return false;
    if (getSessionTimestamp() != null ? !getSessionTimestamp().equals(that.getSessionTimestamp()) : that.getSessionTimestamp() != null)
      return false;
    if (getHealthCheckTimestamp() != null ? !getHealthCheckTimestamp().equals(that.getHealthCheckTimestamp()) : that.getHealthCheckTimestamp() != null)
      return false;
    return getVersion() != null ? getVersion().equals(that.getVersion()) : that.getVersion() == null;
  }

  @Override
  public int hashCode() {
    int result = getSerialId() != null ? getSerialId().hashCode() : 0;
    result = 31 * result + (getSystemUUID() != null ? getSystemUUID().hashCode() : 0);
    result = 31 * result + (getSessionTimestamp() != null ? getSessionTimestamp().hashCode() : 0);
    result = 31 * result + (getHealthCheckTimestamp() != null ? getHealthCheckTimestamp().hashCode() : 0);
    result = 31 * result + (getVersion() != null ? getVersion().hashCode() : 0);
    return result;
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}

