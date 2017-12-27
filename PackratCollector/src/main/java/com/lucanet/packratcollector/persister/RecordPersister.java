package com.lucanet.packratcollector.persister;

import com.lucanet.packratcommon.model.HealthCheckHeader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

public interface RecordPersister {
  // ========================= Interface Variables =========================79
  // ============================ Public Methods ===========================79
  long getOffset(TopicPartition topicPartition);
  void updateOffset(TopicPartition topicPartition, long offset);
  <T> void persistRecord(ConsumerRecord<HealthCheckHeader, T> record);

  // =========================== Default Methods ===========================79
}
