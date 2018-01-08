package com.lucanet.packratreporter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

@SpringBootTest(
    classes = PackratReporter.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration
@TestPropertySource(locations = "classpath:packrat_reporter.properties")
@AutoConfigureMockMvc
public class ReporterStepDefinitions {

  private MockHttpServletResponse servletResponse;
  private MockMvc mockMvc;

  public ReporterStepDefinitions(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @After
  public void teardown() {
    servletResponse = null;
  }

  @When("^the user sends the following request:$")
  public void the_user_sends_the_following_request(List<Map<String, String>> requestMap) throws Throwable {
    Assert.assertThat("Request map must only have one row", requestMap.size(), Matchers.is(1));
    Map<String, String> requestParams = requestMap.get(0);
    String requestUrl = requestParams.get("URL");
    Assert.assertNotNull("URL must not be null", requestUrl);
    mockMvc.perform(MockMvcRequestBuilders.get(requestUrl)).andDo(result -> servletResponse = result.getResponse());
    Assert.assertNotNull(servletResponse);
  }


  @When("^the user requests a list of all serial ids$")
  public void the_user_requests_a_list_of_all_serial_ids() throws Throwable {
    mockMvc.perform(MockMvcRequestBuilders.get("/serialids")).andDo(result -> servletResponse = result.getResponse());
    Assert.assertNotNull(servletResponse);
  }

  @Then("^the response should have an HTTP code of (\\d+)$")
  public void the_response_should_have_an_HTTP_code_of(int statusCode) throws Throwable {
    Assert.assertThat(servletResponse.getStatus(), Matchers.is(statusCode));
  }

  @Then("^the response should have the following HealthCheck map:$")
  public void the_response_should_have_the_following_healthcheck_map(List<Map<String, String>> expectedTable) throws Throwable {
    TypeReference<HashMap<String, ArrayList<String>>> typeReference = new TypeReference<HashMap<String, ArrayList<String>>>() { };
    Map<String, List<String>> responseContent = new ObjectMapper().readValue(servletResponse.getContentAsString(), typeReference);
    expectedTable.forEach(expectedRow -> {
      String healthCheckType = expectedRow.get("HealthCheck Type");
      Assert.assertNotNull("'HealthCheck Type' cannot be null", healthCheckType);
      Assert.assertTrue(
          String.format("Response does not have HealthCheck type '%s'", healthCheckType),
          responseContent.containsKey(healthCheckType)
      );
      List<String> healthCheckTypeValues = responseContent.get(healthCheckType);
      String values = expectedRow.get("Values");
      Assert.assertNotNull("'Values' cannot be null", values);
      String[] separatedValues = values.split(",");
      if ((separatedValues.length > 1) || (!separatedValues[0].isEmpty())) {
        Arrays.stream(separatedValues).forEach(value ->
            Assert.assertTrue(
                String.format("Response does not have '%s' for HealthCheck type '%s', instead has '%s'", healthCheckTypeValues, healthCheckType, value),
                healthCheckTypeValues.contains(value)
            )
        );
      } else {
        Assert.assertTrue(
            String.format("HealthCheck type '%s' should be empty but is not", healthCheckType),
            healthCheckTypeValues.isEmpty()
        );
      }
    });
  }

}
