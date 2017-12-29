package com.lucanet.packratcollector.persister;

import com.lucanet.packratcommon.model.HealthCheckHeader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

/**
 * Interface for interactions with the data persistence layer.
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
public interface RecordPersister {
  // ========================= Interface Variables =========================79
  // ============================ Public Methods ===========================79
  /**
   * Get the message offset for the specified {@link TopicPartition}.
   * @param topicPartition The specified topic and partition.
   * @return The message offset.
   */
  long getOffset(TopicPartition topicPartition);
  /**
   * Set the message offset for the specified {@link TopicPartition}.
   * @param topicPartition The specified topic and partition.
   * @param offset The specified message offset.
   */
  void updateOffset(TopicPartition topicPartition, long offset);
  /**
   * Persist the received HealthCheck message.
   * @param record The HealthCheck message.
   * @param <T> HealthCheck data type.
   */
  <T> void persistRecord(ConsumerRecord<HealthCheckHeader, T> record);

  // =========================== Default Methods ===========================79
}
