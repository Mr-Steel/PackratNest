Feature: HealthCheck Message Collection
  To allow the user to receive HealthCheck messages from a Kafka messaging server

  Background:
    Given a running database instance "packrat_healthcheck_test"
    And a running Apache Kafka Server instance using configuration "kafka_config.properties"

  @Ignore
  @Positive
  Scenario: Receiving HealthCheck Messages
    Given the database instance is populated with HealthCheck data from "empty_db.json"
    And a set of messages defined in "healthcheck_messages.json"
    When the messages are sent to the Apache Kafka Server instance
    Then the "_offsets" collection of the database will have the following entries:
      | topic              | partition | offset |
      | DynamicSystemStats | 0         | 3      |
      | StaticSystemStats  | 0         | 2      |
      | SummaDatabase      | 0         | 2      |
      | TransactionStats   | 0         | 3      |

  @Ignore
  @Negative
  Scenario: Receiving Duplicate HealthCheck Messages
    Given the database instance is populated with HealthCheck data from "fulldata.json"
    And a set of messages defined in "healthcheck_messages.json"
    When the messages are sent to the Apache Kafka Server instance
    Then the "_offsets" collection of the database will have the following entries:
      | topic              | partition | offset |
      | DynamicSystemStats | 0         | 5      |
      | StaticSystemStats  | 0         | 5      |
      | SummaDatabase      | 0         | 3      |
      | TransactionStats   | 0         | 1      |

  @Ignore
  @Negative
  Scenario: Receiving Badly-Formatted HealthCheck Messages
    Given the database instance is populated with HealthCheck data from "empty_db.json"
    And a set of messages defined in "invalid_messages.json"
    When the messages are sent to the Apache Kafka Server instance
    Then the "_offsets" collection of the database will have the following entries:
      | topic              | partition | offset |
      | DynamicSystemStats | 0         | 0      |
      | StaticSystemStats  | 0         | 0      |
      | SummaDatabase      | 0         | 0      |
      | TransactionStats   | 0         | 0      |
