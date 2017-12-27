package com.lucanet.packratcollector.persister;

import com.lucanet.packratcommon.db.DatabaseConnection;
import com.lucanet.packratcommon.model.HealthCheckHeader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

/**
 * Wrapper class around DatabaseConnection to keep loose coupling
 * between Kafka messaging and the database
 */
@Service
public class RecordPersisterImpl implements RecordPersister {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  private final DatabaseConnection databaseConnection;

  // ============================  Constructors  ===========================79
  public RecordPersisterImpl(DatabaseConnection databaseConnection) {
    this.databaseConnection = databaseConnection;
  }

  // ============================ Public Methods ===========================79
  @Override
  public long getOffset(TopicPartition topicPartition) {
    return databaseConnection.getOffset(topicPartition.topic(), topicPartition.partition());
  }

  @Override
  public void updateOffset(TopicPartition topicPartition, long offset) {
    databaseConnection.updateOffset(topicPartition.topic(), topicPartition.partition(), offset);
  }

  @Override
  public <T> void persistRecord(ConsumerRecord<HealthCheckHeader, T> record) {
    databaseConnection.persistRecord(record.topic(), record.key(), record.value());
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
