package com.lucanet.util;

import org.junit.rules.ExternalResource;
import org.springframework.kafka.test.rule.KafkaEmbedded;

public class EmbeddedKafkaWrapper extends ExternalResource {

  private final int brokerCount;
  private final boolean controlledShutdown;
  private final int partitions;
  private final String[] topics;
  private KafkaEmbedded kafkaEmbedded;

  public EmbeddedKafkaWrapper(int brokerCount, boolean controlledShutdown, int partitions, String... topics) {
    this.brokerCount = brokerCount;
    this.controlledShutdown = controlledShutdown;
    this.partitions = partitions;
    this.topics = topics;
    kafkaEmbedded = new KafkaEmbedded(
        this.brokerCount,
        this.controlledShutdown,
        this.partitions,
        this.topics
    );
  }

  public String getBrokers() {
    return kafkaEmbedded.getBrokersAsString();
  }

  public void reset() throws Exception {
    //Kludge to purge the Kafka message topics. Basically, destroy the existing KafkaEmbedded
    //instance and create a new one in its place
    kafkaEmbedded.after();
    kafkaEmbedded = new KafkaEmbedded(
        brokerCount,
        controlledShutdown,
        partitions,
        topics
    );
    kafkaEmbedded.before();
  }

  @Override
  protected void before() throws Throwable {
    kafkaEmbedded.before();
  }

  @Override
  protected void after() {
    kafkaEmbedded.after();
  }
}
