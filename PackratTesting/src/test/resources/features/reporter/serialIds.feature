Feature: Serial Ids Search
  To allow the user to conduct REST requests to retrieve information related to Serial Ids

  Scenario: List Serial Ids
    Given a running database instance "packrat_healthcheck_test"
      And the database instance is populated with HealthCheck data from "fulldata.json"
    When the user sends the following request:
      | URL        |
      | /serialids |
    Then the response should have an HTTP code of 200
      And the response should have the following HealthCheck map:
        | HealthCheck Type   | Values                              |
        | DynamicSystemStats | Serial-AAAA,Serial-BBBB             |
        | StaticSystemStats  | Serial-AAAA,Serial-BBBB,Serial-CCCC |
        | TransactionStats   | Serial-CCCC                         |
        | SummaDatabase      | Serial-BBBB,Serial-DDDD             |

  Scenario Outline: List Systems For Serial Id
    Given a running database instance "packrat_healthcheck_test"
    And the database instance is populated with HealthCheck data from "fulldata.json"
    When the user sends the following request:
      | URL                           |
      | /serialids/<serialId>/systems |
    Then the response should have an HTTP code of 200
      And the response should have the following HealthCheck map:
        | HealthCheck Type   | Values                     |
        | DynamicSystemStats | <DynamicSystemStatsValues> |
        | StaticSystemStats  | <StaticSystemStatsValues>  |
        | SummaDatabase      | <SummaDatabaseValues>      |
        | TransactionStats   | <TransactionStatsValues>   |

    Examples:
      | serialId    | DynamicSystemStatsValues        | StaticSystemStatsValues         | SummaDatabaseValues | TransactionStatsValues |
      | Serial-AAAA | System-AAAA1111,System-AAAA2222 | System-AAAA1111,System-AAAA2222 |                     |                        |
      | Serial-BBBB | System-BBBB1111                 | System-BBBB1111                 | System-BBBB1111     |                        |
      | Serial-CCCC |                                 | System-CCCC1111                 |                     | System-CCCC1111        |
      | Serial-DDDD |                                 |                                 | System-DDDD1111     |                        |
      | Serial-EEEE |                                 |                                 |                     |                        |
