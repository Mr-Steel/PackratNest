package com.lucanet.packratcollector;

import com.lucanet.packratcollector.consumers.MessageConsumer;
import com.lucanet.packratcollector.consumers.MessageConsumerFactory;
import com.lucanet.packratcollector.deserializers.FileLinesDeserializer;
import com.lucanet.packratcollector.deserializers.JSONDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;

/**
 * Runner object for starting the PackratCollector.
 * @see ApplicationRunner
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan({"com.lucanet.packratcollector", "com.lucanet.packratcommon"})
public class PackratCollector implements ApplicationRunner {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  public static void main(String[] args) {
    new SpringApplicationBuilder(PackratCollector.class)
        .web(false)
        .run(args);
  }

  // ============================   Variables    ===========================79
  /**
   * The logger for the PackratCollector instance.
   */
  private final Logger logger;
  /**
   * The list of {@link MessageConsumer} instances that will run during the PackratCollector's runtime.
   */
  private final List<MessageConsumer> messageConsumerList;

  // ============================  Constructors  ===========================79
  /**
   * PackratCollector constructor.
   * @param messageConsumerFactory The producer of the {@link MessageConsumer} that will run during the PackratCollector's runtime.
   * @param jsonTopicsList List of topics that the JSON-based {@link MessageConsumer} instance will monitor for.
   * @param jsonThreadPoolSize Size of thread pool for processing JSON-based HealthCheck messages.
   * @param fileTopicsList List of topics that the file-based {@link MessageConsumer} instance will monitor for.
   * @param fileThreadPoolSize Size of thread pool for processing file-based HealthCheck messages.
   */
  public PackratCollector(
      MessageConsumerFactory messageConsumerFactory,
      @Value("#{'${packrat.consumers.json.topics}'.split(',')}") List<String> jsonTopicsList,
      @Value("${packrat.consumers.json.threadpoolsize}") int jsonThreadPoolSize,
      @Value("#{'${packrat.consumers.file.topics}'.split(',')}") List<String> fileTopicsList,
      @Value("${packrat.consumers.file.threadpoolsize}") int fileThreadPoolSize
  ) {
    logger = LoggerFactory.getLogger(PackratCollector.class);
    messageConsumerList = Arrays.asList(
        messageConsumerFactory.createMessageConsumer("JSONMessageConsumer", JSONDeserializer.class, jsonTopicsList, jsonThreadPoolSize),
        messageConsumerFactory.createMessageConsumer("FileMessageConsumer", FileLinesDeserializer.class, fileTopicsList, fileThreadPoolSize)
    );
  }

  // ============================ Public Methods ===========================79
  /**
   * Initiate the stored {@link MessageConsumer} instances.
   * @param args Passed-in arguments from the application's start-up.
   * @throws Exception Uncaught exceptions that occur during runtime.
   */
  @Override
  public void run(ApplicationArguments args) throws Exception {
    logger.debug("Starting Packrat Collector Message Consumers...");
    messageConsumerList.forEach(MessageConsumer::run);
  }

  /**
   * Shut down the running {@link MessageConsumer} instances.
   */
  @PreDestroy
  public void shutdown() {
    logger.debug("Stopping Packrat Collector Message Consumers...");
    messageConsumerList.forEach(MessageConsumer::stop);
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
