Feature: HealthCheck Search
  To allow the user to conduct REST requests to retrieve information related to HealthChecks

  Background:
    Given a running database instance "packrat_healthcheck_test"

  @Ignore
  @Positive
  Scenario: List HealthCheck types
    When the user sends the following request:
      | URL                 |
      | /healthchecks/types |

    Then the response should have an HTTP code of 200
      And the response should have the following list:
        | DynamicSystemStats, StaticSystemStats, SummaDatabase, TransactionStats |

  @Ignore
  @Positive
  Scenario Outline: List System UUIDs for a HealthCheck type
    Given the database instance is populated with HealthCheck data from "fulldata.json"
    When the user sends the following request:
      | URL                                         |
      | /healthchecks/<healthCheckType>/systemuuids |

    Then the response should have an HTTP code of 200
      And the response should have the following list:
        | <systemUUIDs> |

    Examples:
      | healthCheckType    | systemUUIDs                                                        |
      | DynamicSystemStats | System-AAAA1111, System-AAAA2222, System-BBBB1111                  |
      | StaticSystemStats  | System-AAAA1111, System-AAAA2222, System-BBBB1111, System-CCCC1111 |
      | SummaDatabase      | System-BBBB1111, System-DDDD1111                                   |
      | TransactionStats   | System-CCCC1111                                                    |

  @Ignore
  @Negative
  Scenario: System UUIDs request with an improper HealthCheck type
    Given the database instance is populated with HealthCheck data from "fulldata.json"
    When the user sends the following request:
      | URL                                       |
      | /healthchecks/NonExistentType/systemuuids |

    Then the response should have an HTTP code of 400

  @Ignore
  @Positive
  Scenario Outline: List session timestamps for a SystemUUID in a HealthCheck type
    Given the database instance is populated with HealthCheck data from "fulldata.json"
    When the user sends the following request:
      | URL                                      | systemUUID   |
      | /healthchecks/<healthCheckType>/sessions | <systemUUID> |

    Then the response should have an HTTP code of 200
      And the response should have the following list:
        | <sessionTimestamps> |

    Examples:
      | healthCheckType    | systemUUID      | sessionTimestamps      |
      | DynamicSystemStats | System-AAAA1111 | 1111111111             |
      | DynamicSystemStats | System-BBBB1111 | 1111111122, 1111111123 |
      | SummaDatabase      | System-DDDD1111 | 1111111111             |
      | TransactionStats   | System-EEEE1111 |                        |

  @Ignore
  @Negative
  Scenario: Session timestamps request with an improper HealthCheck type
    Given the database instance is populated with HealthCheck data from "fulldata.json"
    When the user sends the following request:
      | URL                                    | systemUUID      |
      | /healthchecks/NonExistentType/sessions | System-AAAA1111 |

    Then the response should have an HTTP code of 400

  @Ignore
  @Positive
  Scenario Outline: List HealthCheck entries for a SystemUUID in a HealthCheck type for a session
    Given the database instance is populated with HealthCheck data from "fulldata.json"
    When the user sends the following request:
      | URL                                     | systemUUID   | sessionTimestamp   |
      | /healthchecks/<healthCheckType>/entries | <systemUUID> | <sessionTimestamp> |

    Then the response should have an HTTP code of 200
      And the response should match the file content of "<contentFile>"

    Examples:
      | healthCheckType    | systemUUID      | sessionTimestamp | contentFile                   |
      | DynamicSystemStats | System-AAAA1111 | 1111111111       | systemaaaa1111_dynamic11.json |
      | DynamicSystemStats | System-AAAA2222 | 1111111112       | systemaaaa2222_dynamic12.json |
      | SummaDatabase      | System-DDDD1111 | 1111111111       | systemdddd1111_dynamic11.json |
      | StaticSystemStats  | System-EEEE1111 | 1111111111       | systemeeee1111_dynamic11.json |

  @Ignore
  @Negative
  Scenario: HealthCheck entries request with an improper HealthCheck type
    Given the database instance is populated with HealthCheck data from "fulldata.json"
    When the user sends the following request:
      | URL                                   | systemUUID      | sessionTimestamp |
      | /healthchecks/NonExistentType/entries | System-AAAA1111 | 1111111111       |

    Then the response should have an HTTP code of 400
