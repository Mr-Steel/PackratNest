package com.lucanet.packratcollector.persister;

import com.lucanet.packratcommon.model.HealthCheckHeader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

public interface RecordPersister {

  long getOffset(TopicPartition topicPartition);
  void updateOffset(TopicPartition topicPartition, long offset);
  <T> void persistRecord(ConsumerRecord<HealthCheckHeader, T> record);

}
