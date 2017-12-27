package com.lucanet.packratcollector.consumers;

import com.lucanet.packratcollector.persister.RecordPersister;
import com.lucanet.packratcommon.model.HealthCheckHeader;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageConsumerImpl<T> implements MessageConsumer {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  private final Logger                              logger;
  private final String                              consumerName;
  private final KafkaConsumer<HealthCheckHeader, T> kafkaConsumer;
  private final AtomicBoolean                       isRunning;
  private final List<String>                        topicsList;
  private final ExecutorService                     threadPoolExecutor;
  private final RecordPersister                     recordPersister;
  private final Thread                              runnerThread;

  // ============================  Constructors  ===========================79
  MessageConsumerImpl(
      String consumerName,
      Properties kafkaConsumerProperties,
      List<String> topicsList,
      int threadPoolSize,
      RecordPersister recordPersister
  ) {
    this.logger = LoggerFactory.getLogger(MessageConsumerImpl.class);
    this.consumerName = consumerName;
    this.kafkaConsumer = new KafkaConsumer<>(kafkaConsumerProperties);
    this.isRunning = new AtomicBoolean(false);
    this.topicsList = topicsList;
    this.threadPoolExecutor = Executors.newFixedThreadPool(threadPoolSize);
    this.recordPersister = recordPersister;
    this.runnerThread = new Thread(this::runConsumer);
  }

  // ============================ Public Methods ===========================79
  @Override
  public void run() {
    logger.info("Starting consumer {}", consumerName);
    runnerThread.start();
    logger.info("Consumer {} started", consumerName);
  }

  @Override
  public void stop() {
    logger.info("Shutting down consumer {}...", consumerName);
    isRunning.set(false);
    try {
      runnerThread.join();
    } catch (InterruptedException ie) {
      //No-Op
    }
    logger.info("Consumer {} shut down", consumerName);
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
  private void runConsumer() {
    kafkaConsumer.subscribe(topicsList);
    kafkaConsumer.poll(0L);
    topicsList.forEach(topic ->
        kafkaConsumer.partitionsFor(topic).forEach(partitionInfo -> {
          TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
          long topicPartitionOffset = recordPersister.getOffset(topicPartition);
          logger.info("{} setting offset to {} for topic '{}' partition {}", consumerName, topicPartitionOffset, partitionInfo.topic(), partitionInfo.partition());
          kafkaConsumer.seek(topicPartition, topicPartitionOffset);
        })
    );
    isRunning.set(true);
    while (isRunning.get()) {
      try {
        logger.debug("{} polling Kafka Server...", consumerName);
        ConsumerRecords<HealthCheckHeader, T> records = kafkaConsumer.poll(1000L);
        logger.debug("Records polled for {}: {}", consumerName, records.count());
        Observable.fromIterable(records)
            .subscribeOn(Schedulers.from(threadPoolExecutor))
            .subscribe(this::processMessage, this::processError);
      } catch (Exception e) {
        logger.error("Error polling messages in {}: {}", consumerName, e.getMessage());
      }
    }
    kafkaConsumer.close();
  }

  private void processMessage(ConsumerRecord<HealthCheckHeader, T> consumerRecord) {
    try {
      recordPersister.updateOffset(new TopicPartition(consumerRecord.topic(), consumerRecord.partition()), (consumerRecord.offset() + 1));
    } catch (IllegalArgumentException iae) {
      logger.error("{} unable to persist offset for topic '{}' partition {}: {}", consumerName, consumerRecord.topic(), consumerRecord.partition(), iae.getMessage());
    }
    if ((consumerRecord.key() != null) && (consumerRecord.value() != null)) {
      logger.debug("Record received for '{}' in {}: {}", consumerRecord.topic(), consumerName, consumerRecord.value());
      try {
        recordPersister.persistRecord(consumerRecord);
      } catch (IllegalArgumentException iae) {
        logger.error("Unable to write '{}' record {}@{} in {}: topic does not exist in database", consumerRecord.topic(), consumerRecord.offset(), consumerRecord.timestamp(), consumerName);
      }
    } else {
      logger.warn("Unable to process '{}' record {}@{} in {}: either key or value were null", consumerRecord.topic(), consumerRecord.offset(), consumerRecord.timestamp(), consumerName);
    }
  }

  private void processError(Throwable e) {
    logger.error("Error in processing record in {}: {}", consumerName, e.getMessage());
  }
}
