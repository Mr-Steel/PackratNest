package com.lucanet.packratreporter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest(
    classes = PackratReporter.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration
@TestPropertySource(locations = "classpath:packrat_reporter_test.properties")
@AutoConfigureMockMvc
public class ReporterStepDefinitions {

  private final MockMvc mockMvc;
  private final Logger logger;
  private final ObjectMapper objectMapper;
  private MockHttpServletResponse servletResponse;

  public ReporterStepDefinitions(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
    this.logger = LoggerFactory.getLogger(ReporterStepDefinitions.class);
    this.objectMapper = new ObjectMapper();
  }

  @After
  public void teardown() {
    servletResponse = null;
  }

  @When("^the user sends the following request:$")
  public void the_user_sends_the_following_request(List<Map<String, String>> givenMap) throws Throwable {
    Assert.assertThat("Request map must only have one row", givenMap.size(), Matchers.is(1));
    Map<String, String> givenParams = givenMap.get(0);
    String requestUrl = null;
    MultiValueMap<String, String> requestParams = new HttpHeaders();
    for (String paramKey : givenParams.keySet()) {
      String paramValue = givenParams.get(paramKey);
      if (paramKey.equals("URL")) {
        requestUrl = paramValue;
      } else {
        requestParams.add(paramKey, paramValue);
      }
    }
    Assert.assertNotNull("URL must not be null", requestUrl);
    logger.debug("Sending request to URL {} with parameters {}", requestUrl, requestParams);
    mockMvc.perform(MockMvcRequestBuilders.get(requestUrl).params(requestParams)).andDo(result -> servletResponse = result.getResponse());
    Assert.assertNotNull(servletResponse);
  }

  @Then("^the response should have an HTTP code of (\\d+)$")
  public void the_response_should_have_an_HTTP_code_of(int statusCode) throws Throwable {
    Assert.assertThat(servletResponse.getStatus(), Matchers.is(statusCode));
  }

  @Then("^the response should have the following list:$")
  public void the_response_should_have_the_following_list(List<String> givenList) throws Throwable {
    TypeReference<ArrayList<String>> typeReference = new TypeReference<ArrayList<String>>() { };
    List<String> responseList = objectMapper.readValue(servletResponse.getContentAsString(), typeReference);
    Assert.assertThat("Expected list must be only one row", givenList.size(), Matchers.is(1));
    String givenStr = givenList.get(0).trim();
    if (!givenStr.isEmpty()) {
      List<String> expectedList = Arrays.stream(givenList.get(0).split(",")).map(String::trim).collect(Collectors.toList());
      Assert.assertThat(
          String.format("List '%s' does not match expected list '%s'", responseList, expectedList),
          responseList,
          Matchers.containsInAnyOrder(expectedList.toArray())
      );
    } else {
      Assert.assertTrue(
          String.format("List should be empty, instead is '%s'", responseList),
          responseList.isEmpty()
      );
    }
  }

  @Then("^the response should have the following map:$")
  public void the_response_should_have_the_following_map(List<Map<String, String>> givenMap) throws Throwable {
    Assert.assertThat("Expected map must be only one row", givenMap.size(), Matchers.is(1));
    Map<String, String> expectedTable = givenMap.get(0);
    JsonNode responseContent = objectMapper.readTree(servletResponse.getContentAsString());
    for (String expectedKey : expectedTable.keySet()) {
      Assert.assertTrue(
          String.format("Entry for key '%s' not found in response", expectedKey),
          responseContent.has(expectedKey)
      );
      JsonNode expectedValue = objectMapper.readTree(expectedTable.get(expectedKey).trim());
      JsonNode responseValue = responseContent.get(expectedKey);
      Assert.assertThat(
          String.format("Entry '%s' for key '%s' did not match expected '%s'", responseValue, expectedKey, expectedValue),
          responseValue,
          Matchers.is(expectedValue)
      );
    }
  }

  @Then("^the response should match the file content of \"([^\"]*)\"$")
  public void the_response_should_match_the_file_contents_of(String expectedContentsFile) throws Throwable {
    JsonNode expectedJson = objectMapper.readTree(Paths.get(ClassLoader.getSystemResource(String.format("expectedoutput/%s", expectedContentsFile)).toURI()).toFile());
    JsonNode responseJson = objectMapper.readTree(servletResponse.getContentAsString());
    Assert.assertThat(
        String.format("Response '%s' does not match expected '%s'", responseJson, expectedJson),
        responseJson,
        Matchers.is(expectedJson)
    );
  }

}
