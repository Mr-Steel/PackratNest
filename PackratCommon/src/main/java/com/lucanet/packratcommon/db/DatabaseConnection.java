package com.lucanet.packratcommon.db;

import com.lucanet.packratcommon.model.HealthCheckHeader;

import java.util.List;
import java.util.Map;

public interface DatabaseConnection {
  // ========================= Interface Variables =========================79
  String OFFSETS_COLLECTION_NAME = "_offsets";
  String OFFSETS_TOPIC_KEY       = "topic";
  String OFFSETS_PARTITION_KEY   = "partition";
  String OFFSETS_OFFSET_KEY      = "offset";

  // ============================ Public Methods ===========================79
  <T> void persistRecord(String topicName, HealthCheckHeader healthCheckHeader, T record) throws IllegalArgumentException;
  long getOffset(String topicName, int partition) throws IllegalArgumentException;
  void updateOffset(String topicName, int partition, long newOffset) throws IllegalArgumentException;
  List<String> getTopics();
  List<String> getSystemsInTopic(String topicName) throws IllegalArgumentException;
  List<Long> getSessionTimestamps(String topicName, String systemUUID) throws IllegalArgumentException;
  List<Map<String, Object>> getSessionHealthChecks(String topicName, String systemUUID, Long sessionTimestamp) throws IllegalArgumentException;
  Map<String, List<String>> getSerialIDS();
  Map<String, List<String>> getSystemsForSerialID(String serialID);

  // =========================== Default Methods ===========================79
}
