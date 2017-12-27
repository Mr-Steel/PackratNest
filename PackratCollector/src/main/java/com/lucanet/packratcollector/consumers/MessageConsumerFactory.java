package com.lucanet.packratcollector.consumers;

import org.apache.kafka.common.serialization.Deserializer;

import java.util.List;

public interface MessageConsumerFactory {
  // ========================= Interface Variables =========================79
  // ============================ Public Methods ===========================79
  <T> MessageConsumer createMessageConsumer(
      String consumerName,
      Class<? extends Deserializer> valueDeserializerClass,
      List<String> topicsList,
      int threadpoolSize
  );

  // =========================== Default Methods ===========================79
}
