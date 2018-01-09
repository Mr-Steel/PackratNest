package com.lucanet.packratcollector;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
    classes = PackratCollector.class
)
@ContextConfiguration
@TestPropertySource(locations = "classpath:packrat_collector.properties")
public class CollectorStepDefinitions {

  private KafkaEmbedded kafkaEmbedded;

  @After
  public void teardown() throws Exception {

  }

  @Given("^a running Apache Kafka Server instance using configuration \"([^\"]*)\"$")
  public void a_running_Apache_Kafka_Server_instance(String configFileLocation) throws Throwable {
    kafkaEmbedded = new KafkaEmbedded(1);
  }

  @Given("^a set of messages defined in \"([^\"]*)\"$")
  public void a_set_of_messages_defined_in(String messagesFile) throws Throwable {
  }

  @When("^the messages are sent to the Apache Kafka Server instance$")
  public void the_messages_are_sent_to_the_Apache_Kafka_Server_instance() throws Throwable {

  }
}
