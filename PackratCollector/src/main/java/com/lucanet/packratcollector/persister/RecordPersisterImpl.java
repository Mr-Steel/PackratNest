package com.lucanet.packratcollector.persister;

import com.lucanet.packratcommon.db.DatabaseConnection;
import com.lucanet.packratcommon.model.HealthCheckHeader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

/**
 * Wrapper class around DatabaseConnection to keep loose coupling between Kafka messaging and the database.
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
@Service
public class RecordPersisterImpl implements RecordPersister {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  /**
   * The database persistence object.
   */
  private final DatabaseConnection databaseConnection;

  // ============================  Constructors  ===========================79
  /**
   * Persister constructor.
   * @param databaseConnection The database persistence object.
   */
  public RecordPersisterImpl(DatabaseConnection databaseConnection) {
    this.databaseConnection = databaseConnection;
  }

  // ============================ Public Methods ===========================79
  /**
   * Get the message offset for the specified {@link TopicPartition}.
   * @param topicPartition The specified topic and partition.
   * @return The message offset.
   */
  @Override
  public long getOffset(TopicPartition topicPartition) {
    return databaseConnection.getOffset(topicPartition.topic(), topicPartition.partition());
  }

  /**
   * Set the message offset for the specified {@link TopicPartition}.
   * @param topicPartition The specified topic and partition.
   * @param offset The specified message offset.
   */
  @Override
  public void updateOffset(TopicPartition topicPartition, long offset) {
    databaseConnection.updateOffset(topicPartition.topic(), topicPartition.partition(), offset);
  }

  /**
   * Persist the received HealthCheck message.
   * @param record The HealthCheck message.
   * @param <T> HealthCheck data type.
   */
  @Override
  public <T> void persistRecord(ConsumerRecord<HealthCheckHeader, T> record) {
    databaseConnection.persistRecord(record.topic(), record.key(), record.value());
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
