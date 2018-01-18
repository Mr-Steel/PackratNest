Feature: HealthCheck Message Collection
  To allow the user to receive HealthCheck messages from a Kafka messaging server

  Background:
    Given a running database instance "packrat_healthcheck_test"
    And a connection to the Kafka broker

  @Positive
  Scenario: Receiving HealthCheck Messages
    Given the database instance is populated with HealthCheck data from "empty_db.json"
      And a set of messages defined in "healthcheck_messages.json"
    When the messages are sent to the Apache Kafka Server instance
      And I wait 5 seconds
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
      | Name                 | Value           | Type    |
      | serialId             | Serial-AAAA     | String  |
      | systemUUID           | System-AAAA1111 | String  |
      | sessionTimestamp     | 1111111111      | Long    |
      | healthCheckTimestamp | 1111111111      | Long    |
      | version              | 1               | Integer |
    And the "StaticSystemStats" collection of the database will have an entry with the following attributes:
      | Name                 | Value           | Type    |
      | serialId             | Serial-AAAA     | String  |
      | systemUUID           | System-AAAA2222 | String  |
      | sessionTimestamp     | 1111111112      | Long    |
      | healthCheckTimestamp | 1111111112      | Long    |
      | version              | 1               | Integer |
    And the "SummaDatabase" collection of the database will have an entry with the following attributes:
      | Name                 | Value           | Type    |
      | serialId             | Serial-BBBB     | String  |
      | systemUUID           | System-BBBB1111 | String  |
      | sessionTimestamp     | 1111111123      | Long    |
      | healthCheckTimestamp | 1111111123      | Long    |
      | version              | 1               | Integer |
    And the "TransactionStats" collection of the database will have an entry with the following attributes:
      | Name                 | Value           | Type    |
      | serialId             | Serial-CCCC     | String  |
      | systemUUID           | System-CCCC1111 | String  |
      | sessionTimestamp     | 1111111111      | Long    |
      | healthCheckTimestamp | 1111111111      | Long    |
      | version              | 1               | Integer |

  @Negative
  Scenario: Receiving Duplicate HealthCheck Messages
    Given the database instance is populated with HealthCheck data from "empty_db.json"
      And a set of messages defined in "duplicate_messages.json"
    When the messages are sent to the Apache Kafka Server instance
      And I wait 1 seconds
      And the messages are sent to the Apache Kafka Server instance
      And I wait 5 seconds
    Then the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value              | Type    |
      | topic     | DynamicSystemStats | String  |
      | partition | 0                  | Integer |
      | offset    | 4                  | Long    |
    And the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value             | Type    |
      | topic     | StaticSystemStats | String  |
      | partition | 0                 | Integer |
      | offset    | 4                 | Long    |
    And the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value         | Type    |
      | topic     | SummaDatabase | String  |
      | partition | 0             | Integer |
      | offset    | 2             | Long    |
    And the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value            | Type    |
      | topic     | TransactionStats | String  |
      | partition | 0                | Integer |
      | offset    | 2                | Long    |
    #If the duplicate message for this entry is not rejected, then this step will fail, as there will be
    #two such entries in the collection
    And the "DynamicSystemStats" collection of the database will have an entry with the following attributes:
      | Name                 | Value           | Type    |
      | serialId             | Serial-AAAA     | String  |
      | systemUUID           | System-AAAA1111 | String  |
      | sessionTimestamp     | 1111111111      | Long    |
      | healthCheckTimestamp | 1111111111      | Long    |
      | version              | 1               | Integer |
    #If the duplicate message for this entry is not rejected, then this step will fail, as there will be
    #two such entries in the collection
    And the "StaticSystemStats" collection of the database will have an entry with the following attributes:
      | Name                 | Value           | Type    |
      | serialId             | Serial-AAAA     | String  |
      | systemUUID           | System-AAAA2222 | String  |
      | sessionTimestamp     | 1111111112      | Long    |
      | healthCheckTimestamp | 1111111112      | Long    |
      | version              | 1               | Integer |

  @Negative
  Scenario: Rejecting Badly-Formatted HealthCheck Messages
    Given the database instance is populated with HealthCheck data from "empty_db.json"
      And a set of messages defined in "invalid_messages.json"
    When the messages are sent to the Apache Kafka Server instance
      And I wait 5 seconds
    Then the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value              | Type    |
      | topic     | DynamicSystemStats | String  |
      | partition | 0                  | Integer |
      | offset    | 1                  | Long    |
    And the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value             | Type    |
      | topic     | StaticSystemStats | String  |
      | partition | 0                 | Integer |
      | offset    | 4                 | Long    |
    And the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value         | Type    |
      | topic     | SummaDatabase | String  |
      | partition | 0             | Integer |
      | offset    | 2             | Long    |
    And the "_offsets" collection of the database will have an entry with the following attributes:
      | Name      | Value            | Type    |
      | topic     | TransactionStats | String  |
      | partition | 0                | Integer |
      | offset    | 2                | Long    |
    And the "DynamicSystemStats" collection will be empty
    And the "StaticSystemStats" collection will be empty
    And the "SummaDatabase" collection will be empty
    And the "TransactionStats" collection will be empty
