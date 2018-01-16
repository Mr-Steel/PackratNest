package com.lucanet.packratcollector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucanet.packratcommon.model.HealthCheckHeader;
import com.lucanet.packratcommon.model.HealthCheckRecord;
import com.lucanet.util.JSONObjectSerializer;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.*;
import java.util.function.Function;


@SpringBootTest(
    classes = PackratCollector.class
)
@ContextConfiguration
@DirtiesContext
@TestPropertySource(locations = "classpath:packrat_collector_test.properties")
public class CollectorStepDefinitions {

  private static final List<String> JSON_DATA_TOPICS = Arrays.asList(
      "DynamicSystemStats",
      "StaticSystemStats",
      "SummaDatabase"
  );
  private static final List<String> FILE_DATA_TOPICS = Collections.singletonList("TransactionStats");

  private ObjectMapper objectMapper = new ObjectMapper();
  private Map<String, Map<HealthCheckHeader, JsonNode>> jsonMessagesMap = new HashMap<>();
  private Map<String, Map<HealthCheckHeader, List<String>>> fileMessagesMap = new HashMap<>();

  private KafkaTemplate<HealthCheckHeader, JsonNode> jsonMessagesTemplate;
  private KafkaTemplate<HealthCheckHeader, List<String>> fileMessagesTemplate;

  @After
  public void teardown() throws Exception {
    jsonMessagesTemplate = null;
    jsonMessagesMap.clear();
    fileMessagesTemplate = null;
    fileMessagesMap.clear();
    PackratCollectorTest.embeddedKafkaWrapper.reset();
    Thread.sleep(1000L);
  }

  @Given("^a connection to the Kafka broker$")
  public void a_connection_to_the_kafka_broker() throws Throwable {
    Map<String, Object> senderProperties = KafkaTestUtils.senderProps(PackratCollectorTest.embeddedKafkaWrapper.getBrokers());
    senderProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JSONObjectSerializer.class);
    senderProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JSONObjectSerializer.class);
    ProducerFactory<HealthCheckHeader, JsonNode> jsonMessageProducerFactory =
        new DefaultKafkaProducerFactory<>(senderProperties);
    jsonMessagesTemplate = new KafkaTemplate<>(jsonMessageProducerFactory);
    ProducerFactory<HealthCheckHeader, List<String>> fileMessageProducerFactory =
        new DefaultKafkaProducerFactory<>(senderProperties);
    fileMessagesTemplate = new KafkaTemplate<>(fileMessageProducerFactory);
  }

  @SuppressWarnings("unchecked")
  @Given("^a set of messages defined in \"([^\"]*)\"$")
  public void a_set_of_messages_defined_in(String messagesFileName) throws Throwable {
    //Load test messages data file
    JsonNode messagesNode = objectMapper.readTree(ClassLoader.getSystemResourceAsStream(String.format("messages/%s", messagesFileName)));
    //Populate JSON-based messages
    JSON_DATA_TOPICS.forEach(jsonDataTopic -> {
      Map<HealthCheckHeader, JsonNode> topicMap = new HashMap<>();
      messagesNode.get(jsonDataTopic).elements().forEachRemaining(messageNode ->
          populateJsonEntry(topicMap, messageNode, dataNode -> objectMapper.convertValue(dataNode, JsonNode.class))
      );
      jsonMessagesMap.put(jsonDataTopic, topicMap);
    });
    //Populate File-based messages
    FILE_DATA_TOPICS.forEach(fileDataTopic -> {
      Map<HealthCheckHeader, List<String>> topicMap = new HashMap<>();
      messagesNode.get(fileDataTopic).elements().forEachRemaining(messageNode ->
          populateJsonEntry(topicMap, messageNode, dataNode -> objectMapper.convertValue(dataNode, List.class))
      );
      fileMessagesMap.put(fileDataTopic, topicMap);
    });
  }

  @When("^the messages are sent to the Apache Kafka Server instance$")
  public void the_messages_are_sent_to_the_Apache_Kafka_Server_instance() throws Throwable {
    jsonMessagesMap.forEach((jsonTopic, jsonMessages) ->
      jsonMessages.forEach((header, data) ->
        jsonMessagesTemplate.send(jsonTopic, header, data)
      )
    );
    fileMessagesMap.forEach((fileTopic, fileMessages) ->
      fileMessages.forEach((header, data) ->
          fileMessagesTemplate.send(fileTopic, header, data)
      )
    );
  }

  private <T> void populateJsonEntry(Map<HealthCheckHeader, T> topicMap, JsonNode messageNode, Function<JsonNode, T> dataTransformerFunction) {
    HealthCheckHeader healthCheckHeader = new HealthCheckHeader();
    //Set Serial Id
    JsonNode serialIdNode = messageNode.get(HealthCheckRecord.SERIAL_ID);
    healthCheckHeader.setSerialId(
        !serialIdNode.isNull() ?
            serialIdNode.asText() :
            null
    );
    //Set System UUID
    JsonNode systemUUIDNode = messageNode.get(HealthCheckRecord.SYSTEM_UUID);
    healthCheckHeader.setSystemUUID(
        !systemUUIDNode.isNull() ?
            systemUUIDNode.asText() :
            null
    );
    //Set Session Timestamp
    JsonNode sessionTimestampNode = messageNode.get(HealthCheckRecord.SESSION_TIMESTAMP);
    healthCheckHeader.setSessionTimestamp(
        !sessionTimestampNode.isNull() ?
            sessionTimestampNode.longValue() :
            null
    );
    //Set HealthCheck Timestamp
    JsonNode healthCheckTimestampNode = messageNode.get(HealthCheckRecord.HEALTHCHECK_TIMESTAMP);
    healthCheckHeader.setHealthCheckTimestamp(
        !healthCheckTimestampNode.isNull() ?
            healthCheckTimestampNode.longValue() :
            null
    );
    //Set Version
    JsonNode versionNode = messageNode.get(HealthCheckRecord.VERSION);
    healthCheckHeader.setVersion(
        !versionNode.isNull() ?
            versionNode.intValue() :
            null
    );
    topicMap.put(healthCheckHeader, dataTransformerFunction.apply(messageNode.get(HealthCheckRecord.DATA)));
  }
}
