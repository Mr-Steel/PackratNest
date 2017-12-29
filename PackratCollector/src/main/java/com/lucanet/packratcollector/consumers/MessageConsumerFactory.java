package com.lucanet.packratcollector.consumers;

import org.apache.kafka.common.serialization.Deserializer;

import java.util.List;

/**
 * Interface for the factory object that will produce instances of {@link MessageConsumer}.
 * @see MessageConsumer
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
public interface MessageConsumerFactory {
  // ========================= Interface Variables =========================79
  // ============================ Public Methods ===========================79
  /**
   * Produce an instance of a {@link MessageConsumer}.
   * @param consumerName The name of the message consumer. This is used for identification purposes during logging.
   * @param valueDeserializerClass The class that will deserialize the HealthCheck message data.
   * @param topicsList The list of topics that the message consumer will listen for incoming HealthCheck messages.
   * @param threadpoolSize Thread pool for executing the HealthCheck message consumption callback separately from the message retrieval thread.
   * @param <T> The type of the HealthCheck message data.
   * @return The produced {@link MessageConsumer}
   */
  <T> MessageConsumer createMessageConsumer(
      String consumerName,
      Class<? extends Deserializer<T>> valueDeserializerClass,
      List<String> topicsList,
      int threadpoolSize
  );

  // =========================== Default Methods ===========================79
}
