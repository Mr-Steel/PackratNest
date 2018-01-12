Feature: Serial Ids Search
  To allow the user to conduct REST requests to retrieve information related to Serial Ids

  Background:
    Given a running database instance "packrat_healthcheck_test"
      And the database instance is populated with HealthCheck data from "fulldata.json"

  @Ignore
  @Positive
  Scenario: List Serial Ids
    When the user sends the following request:
      | URL        |
      | /serialids |
    Then the response should have an HTTP code of 200
      And the response should have the following map:
        | DynamicSystemStats            | StaticSystemStats                           | SummaDatabase                 | TransactionStats |
        | ["Serial-AAAA","Serial-BBBB"] | ["Serial-AAAA","Serial-BBBB","Serial-CCCC"] | ["Serial-BBBB","Serial-DDDD"] | ["Serial-CCCC"]  |

  @Ignore
  @Positive
  Scenario Outline: List Systems For Serial Id
    When the user sends the following request:
      | URL                           |
      | /serialids/<serialId>/systems |
    Then the response should have an HTTP code of 200
      And the response should have the following map:
        | DynamicSystemStats   | StaticSystemStats   | SummaDatabase   | TransactionStats   |
        | <dynamicSystemStats> | <staticSystemStats> | <summaDatabase> | <transactionStats> |

    Examples:
      | serialId    | dynamicSystemStats                    | staticSystemStats                     | summaDatabase       | transactionStats    |
      | Serial-AAAA | ["System-AAAA2222","System-AAAA1111"] | ["System-AAAA2222","System-AAAA1111"] | []                  | []                  |
      | Serial-BBBB | ["System-BBBB1111"]                   | ["System-BBBB1111"]                   | ["System-BBBB1111"] | []                  |
      | Serial-CCCC | []                                    | ["System-CCCC1111"]                   | []                  | ["System-CCCC1111"] |
      | Serial-DDDD | []                                    | []                                    | ["System-DDDD1111"] | []                  |
      | Serial-EEEE | []                                    | []                                    | []                  | []                  |
