package com.lucanet.packratcollector.persister;

import com.lucanet.packratcommon.db.DatabaseConnection;
import com.lucanet.packratcommon.model.HealthCheckHeader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RecordPersisterImpl implements RecordPersister {

  private final Logger             logger;
  private final DatabaseConnection databaseConnection;

  public RecordPersisterImpl(DatabaseConnection databaseConnection) {
    this.logger = LoggerFactory.getLogger(RecordPersisterImpl.class);
    this.databaseConnection = databaseConnection;
  }

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
}
