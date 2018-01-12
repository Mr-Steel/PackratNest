Feature: HealthCheck Message Collection
  To allow the user to receive HealthCheck messages from a Kafka messaging server

  Background:
    Given a running database instance "packrat_healthcheck_test"
    And a connection to the Kafka broker

  @Ignore
  @Positive
  Scenario: Receiving HealthCheck Messages
    Given the database instance is populated with HealthCheck data from "empty_db.json"
    And a set of messages defined in "healthcheck_messages.json"
    When the messages are sent to the Apache Kafka Server instance
    And I wait 2 seconds
    Then the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value              | Type    |
      | topic     | DynamicSystemStats | String  |
      | partition | 0                  | Integer |
      | offset    | 1                  | Long    |
    And the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value             | Type    |
      | topic     | StaticSystemStats | String  |
      | partition | 0                 | Integer |
      | offset    | 1                 | Long    |
    And the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value         | Type    |
      | topic     | SummaDatabase | String  |
      | partition | 0             | Integer |
      | offset    | 1             | Long    |
    And the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value            | Type    |
      | topic     | TransactionStats | String  |
      | partition | 0                | Integer |
      | offset    | 1                | Long    |
    And the "DynamicSystemStats" collection of the database will have an entry with the following attributes:
      | Name                 | Value           | Type   |
      | serialId             | Serial-AAAA     | String |
      | systemUUID           | System-AAAA1111 | String |
      | sessionTimestamp     | 1111111111      | Long   |
      | healthCheckTimestamp | 1111111111      | Long   |
    And the "StaticSystemStats" collection of the database will have an entry with the following attributes:
      | Name                 | Value           | Type   |
      | serialId             | Serial-AAAA     | String |
      | systemUUID           | System-AAAA2222 | String |
      | sessionTimestamp     | 1111111112      | Long   |
      | healthCheckTimestamp | 1111111112      | Long   |
    And the "SummaDatabase" collection of the database will have an entry with the following attributes:
      | Name                 | Value           | Type   |
      | serialId             | Serial-BBBB     | String |
      | systemUUID           | System-BBBB1111 | String |
      | sessionTimestamp     | 1111111123      | Long   |
      | healthCheckTimestamp | 1111111123      | Long   |
    And the "TransactionStats" collection of the database will have an entry with the following attributes:
      | Name                 | Value           | Type   |
      | serialId             | Serial-CCCC     | String |
      | systemUUID           | System-CCCC1111 | String |
      | sessionTimestamp     | 1111111111      | Long   |
      | healthCheckTimestamp | 1111111111      | Long   |

  @Ignore
  @Negative
  Scenario: Receiving Duplicate HealthCheck Messages
    Given the database instance is populated with HealthCheck data from "fulldata.json"
    And a set of messages defined in "healthcheck_messages.json"
    When the messages are sent to the Apache Kafka Server instance
    And I wait 2 seconds
    Then the "_offsets" collection of the database will have the following entries:
      | topic              | partition | offset |
      | DynamicSystemStats | 0         | 5      |
      | StaticSystemStats  | 0         | 5      |
      | SummaDatabase      | 0         | 3      |
      | TransactionStats   | 0         | 1      |

  @Ignore
  @Negative
  Scenario: Rejecting Badly-Formatted HealthCheck Messages
    Given the database instance is populated with HealthCheck data from "empty_db.json"
    And a set of messages defined in "invalid_messages.json"
    When the messages are sent to the Apache Kafka Server instance
    And I wait 2 seconds
    Then the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value              | Type    |
      | topic     | DynamicSystemStats | String  |
      | partition | 0                  | Integer |
      | offset    | 1                  | Long    |
    And the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value             | Type    |
      | topic     | StaticSystemStats | String  |
      | partition | 0                 | Integer |
      | offset    | 1                 | Long    |
    And the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value         | Type    |
      | topic     | SummaDatabase | String  |
      | partition | 0             | Integer |
      | offset    | 1             | Long    |
    And the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value            | Type    |
      | topic     | TransactionStats | String  |
      | partition | 0                | Integer |
      | offset    | 0                | Long    |
    And the "DynamicSystemStats" collection will be empty
    And the "StaticSystemStats" collection will be empty
    And the "SummaDatabase" collection will be empty
    And the "TransactionStats" collection will be empty
