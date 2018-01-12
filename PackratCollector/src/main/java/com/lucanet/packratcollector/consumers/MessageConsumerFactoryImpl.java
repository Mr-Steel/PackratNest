package com.lucanet.packratcollector.consumers;

import com.lucanet.packratcollector.config.PackratCollectorConfig;
import com.lucanet.packratcollector.persister.RecordPersister;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

/**
 * Implementation of {@link MessageConsumerFactory} that creates instances of {@link MessageConsumerImpl}
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
@Component
public class MessageConsumerFactoryImpl implements MessageConsumerFactory {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  /**
   * The common message consumer configuration properties.
   */
  private final PackratCollectorConfig packratCollectorConfig;
  /**
   * Entity that will persist retrieved HealthCheck messages for later analysis.
   */
  private final RecordPersister recordPersister;

  // ============================  Constructors  ===========================79
  /**
   * Consumer factory constructor.
   * @param packratCollectorConfig The common message consumer configuration properties.
   * @param recordPersister Entity that will persist retrieved HealthCheck messages for later analysis.
   */
  public MessageConsumerFactoryImpl(PackratCollectorConfig packratCollectorConfig, RecordPersister recordPersister) {
    this.packratCollectorConfig = packratCollectorConfig;
    this.recordPersister = recordPersister;
  }

  // ============================ Public Methods ===========================79
  /**
   * Produce an instance of a {@link MessageConsumerImpl}.
   * @param consumerName The name of the message consumer. This is used for identification purposes during logging.
   * @param valueDeserializerClass The class that will deserialize the HealthCheck message data.
   * @param topicsList The list of topics that the message consumer will listen for incoming HealthCheck messages.
   * @param threadpoolSize Thread pool for executing the HealthCheck message consumption callback separately from the message retrieval thread.
   * @param <T> The type of the HealthCheck message data.
   * @return The produced {@link MessageConsumerImpl}
   */
  @Override
  public <T> MessageConsumer createMessageConsumer(String consumerName, Class<? extends Deserializer<T>> valueDeserializerClass, List<String> topicsList, int threadpoolSize) {
    Properties messageConsumerProperties = packratCollectorConfig.generateCommonProperties();
    messageConsumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass.getCanonicalName());
    return new MessageConsumerImpl<T>(
        consumerName,
        messageConsumerProperties,
        topicsList,
        packratCollectorConfig.getBrokerConnectTimeout(),
        packratCollectorConfig.getBrokerPollTimeout(),
        threadpoolSize,
        recordPersister
    );
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
