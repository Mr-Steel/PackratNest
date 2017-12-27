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
import java.util.Map;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"com.lucanet.packratcollector", "com.lucanet.packratcommon"})
public class PackratCollector implements ApplicationRunner {
  public static void main(String[] args) {
    new SpringApplicationBuilder(PackratCollector.class)
        .web(false)
        .run(args);
  }

  private final Logger logger;
  private final List<MessageConsumer> messageConsumerList;

  public PackratCollector(
      MessageConsumerFactory messageConsumerFactory,
      @Value("#{'${packrat.consumers.json.topics}'.split(',')}") List<String> jsonTopicsList,
      @Value("${packrat.consumers.json.threadpoolsize}") int jsonThreadPoolSize,
      @Value("#{'${packrat.consumers.file.topics}'.split(',')}") List<String> fileTopicsList,
      @Value("${packrat.consumers.file.threadpoolsize}") int fileThreadPoolSize
  ) {
    logger = LoggerFactory.getLogger(PackratCollector.class);
    messageConsumerList = Arrays.asList(
        messageConsumerFactory.<Map<String, Object>>createMessageConsumer("JSONMessageConsumer", JSONDeserializer.class, jsonTopicsList, jsonThreadPoolSize),
        messageConsumerFactory.<List<String>>createMessageConsumer("FileMessageConsumer", FileLinesDeserializer.class, fileTopicsList, fileThreadPoolSize)
    );
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    logger.debug("Starting Packrat Collector Message Consumers...");
    messageConsumerList.forEach(MessageConsumer::run);
  }

  @PreDestroy
  public void shutdown() {
    logger.debug("Stopping Packrat Collector Message Consumers...");
    messageConsumerList.forEach(MessageConsumer::stop);
  }
}
