package com.lucanet.packratcollector.config;

import com.lucanet.packratcollector.deserializers.HealthCheckHeaderDeserializer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Configuration class for generating common configuration properties for the HealthCheck data Kafka message consumers.
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
@Configuration
public class PackratCollectorConfig {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  /**
   * @see CommonClientConfigs#BOOTSTRAP_SERVERS_DOC
   */
  private final String bootstrapServers;
  /**
   * @see ConsumerConfig#GROUP_ID_DOC
   */
  private final String groupId;
  /**
   * @see ConsumerConfig#AUTO_COMMIT_INTERVAL_MS_DOC
   */
  private final int autoCommitInterval;
  /**
   * @see ConsumerConfig#SESSION_TIMEOUT_MS_DOC
   */
  private final int sessionTimeout;
  /**
   * @see com.lucanet.packratcollector.consumers.MessageConsumerImpl#brokerConnectTimeout
   */
  private final long brokerConnectTimeout;
  /**
   * @see com.lucanet.packratcollector.consumers.MessageConsumerImpl#brokerPollTimeout
   */
  private final long brokerPollTimeout;

  // ============================  Constructors  ===========================79
  /**
   * Configuration constructor.
   * @param bootstrapServers The comma-delineated list of Kafka server addresses.
   * @param groupId The id of the Kafka message consumer group to which the Packrat Collector belongs.
   * @param autoCommitInterval The interval of the auto-commit message sent to the Kafka message server.
   * @param sessionTimeout The timeout interval for the Kafka message consumer session.
   * @param brokerPollTimeout The timeout interval for polling the Kafka broker.
   */
  public PackratCollectorConfig(
      @Value("${packrat.bootstrapServers}") String bootstrapServers,
      @Value("${packrat.groupId}") String groupId,
      @Value("${packrat.autoCommitInterval}") int autoCommitInterval,
      @Value("${packrat.sessionTimeout}") int sessionTimeout,
      @Value("${packrat.broker.connectTimeout}") long brokerConnectTimeout,
      @Value("${packrat.broker.pollTimeout}") long brokerPollTimeout
  ) {
    this.bootstrapServers = bootstrapServers;
    this.groupId = groupId;
    this.autoCommitInterval = autoCommitInterval;
    this.sessionTimeout = sessionTimeout;
    this.brokerConnectTimeout = brokerConnectTimeout;
    this.brokerPollTimeout = brokerPollTimeout;
  }

  // ============================ Public Methods ===========================79
  /**
   * Generate the {@link Properties} object which contains the common Kafka configuration settings.
   * @return The common properties object.
   */
  public Properties generateCommonProperties() {
    Properties props = new Properties();
    props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, HealthCheckHeaderDeserializer.class.getCanonicalName());
    return props;
  }

  public long getBrokerConnectTimeout() {
    return brokerConnectTimeout;
  }

  public long getBrokerPollTimeout() {
    return brokerPollTimeout;
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
