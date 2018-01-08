package com.lucanet.packratcommon.db;

import com.lucanet.packratcommon.model.HealthCheckHeader;

import java.util.List;
import java.util.Map;

/**
 * Interface for interaction between Packrat Components and the data persistence implementation.
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
public interface DatabaseConnection {
  // ========================= Interface Variables =========================79
  // ============================ Public Methods ===========================79
  /**
   * Persist a HealthCheck record.
   * @param healthCheckType The type of the HealthCheck record.
   * @param healthCheckHeader The HealthCheck's metadata.
   * @param record The HealthCheck record.
   * @param <T> The HealthCheck data type.
   * @throws IllegalArgumentException Signifies that the HealthCheck type does not exist in the database.
   */
  <T> void persistRecord(String healthCheckType, HealthCheckHeader healthCheckHeader, T record) throws IllegalArgumentException;
  /**
   * Get the message offset for the specified HealthCheck type and message partition.
   * @param healthCheckType The specified HealthCheck type.
   * @param partition The message partition for the HealthCheck type.
   * @return The message offset.
   * @throws IllegalArgumentException Signifies that the HealthCheck type does not exist in the database.
   */
  long getOffset(String healthCheckType, int partition) throws IllegalArgumentException;
  /**
   * Set the new message offset for the specified HealthCheck type and message partition.
   * @param healthCheckType The specified HealthCheck type.
   * @param partition The message partition for the HealthCheck type.
   * @param newOffset The new message offset.
   * @throws IllegalArgumentException Signifies that the HealthCheck type does not exist in the database.
   */
  void updateOffset(String healthCheckType, int partition, long newOffset) throws IllegalArgumentException;
  /**
   * Request a list of HealthCheck types that the database persists.
   * @return The list of persistable HealthCheck type.
   */
  List<String> getHealthCheckTypes();
  /**
   * Request a list of all computer instances that have HealthCheck records stored for the specified HealthCheck type.
   * @param healthCheckType The specified HealthCheck type.
   * @return The list of relevant computers (in the form of UUID entities).
   * @throws IllegalArgumentException Signifies that the HealthCheck type does not exist in the database.
   */
  List<String> getSystemsInHealthCheckType(String healthCheckType) throws IllegalArgumentException;
  /**
   * Request a list of all sessions in which a computer has HealthCheck records stored for the specified HealthCheck type.
   * @param healthCheckType The specified HealthCheck type.
   * @param systemUUID The specified computer (in the form of a UUID entity).
   * @return The list of sessions (in the form of timestamps representing seconds elapsed since the UNIX epoch).
   * @throws IllegalArgumentException Signifies that the HealthCheck type does not exist in the database.
   */
  List<Long> getSessionTimestamps(String healthCheckType, String systemUUID) throws IllegalArgumentException;
  /**
   * Request a list of all HealthCheck records for a specified computer's session correlating to a HealthCheck type.
   * @param healthCheckType The specified HealthCheck type.
   * @param systemUUID The specified computer (in the form of a UUID entity).
   * @param sessionTimestamp The specified session (in the form of a timestamp representing seconds elapsed since the UNIX epoch).
   * @return The list of all HealthCheck records for the computer that occurred in the specified session.
   * @throws IllegalArgumentException Signifies that the HealthCheck type does not exist in the database.
   */
  List<Map<String, Object>> getSessionHealthChecks(String healthCheckType, String systemUUID, Long sessionTimestamp) throws IllegalArgumentException;
  /**
   * Request a map of all computer groups that have records in each HealthCheck type.
   * @return The map of all computer groups present (in the form of a serial ID for each computer group).
   */
  Map<String, List<String>> getSerialIds();
  /**
   * Request a map of all computers that have records in each HealthCheck type for a specified computer group.
   * @param serialId The specified computer group (in the form of a serial ID).
   * @return The map of all computers (in the form of UUID entities) in a computer group that have records stored for each HealthCheck type.
   */
  Map<String, List<String>> getSystemsForSerialID(String serialId);
  /**
   * Shut down the database connection
   */
  void shutdown();
  // =========================== Default Methods ===========================79
}
