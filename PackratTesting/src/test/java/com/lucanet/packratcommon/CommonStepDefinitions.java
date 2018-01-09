package com.lucanet.packratcommon;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CommonStepDefinitions {

  private static final List<String> COLLECTIONS_LIST = Arrays.asList(
      "DynamicSystemStats",
      "StaticSystemStats",
      "SummaDatabase",
      "TransactionStats"
  );

  private MongoClient mongoClient;
  private MongoDatabase mongoDatabase;
  private ObjectMapper objectMapper = new ObjectMapper();
  private TypeReference<HashMap<String, ArrayList<HashMap<String, Object>>>> typeReference = new TypeReference<HashMap<String, ArrayList<HashMap<String, Object>>>>(){};

  @After
  public void teardown() {
    if (mongoDatabase != null) {
      clearDatabase(mongoDatabase);
      mongoDatabase = null;
    }
    if (mongoClient != null) {
      mongoClient.close();
      mongoClient = null;
    }
  }

  @Given("^a running database instance \"([^\"]*)\"$")
  public void a_running_database_instance(String dbName) throws Throwable {
    MongoClientOptions.Builder clientOptionsBuilder = new MongoClientOptions.Builder();
    mongoClient = new MongoClient(
        new ServerAddress("localhost", 27017),
        MongoCredential.createCredential("packratUser", dbName, "packratPassword".toCharArray()),
        clientOptionsBuilder.build()
    );
    mongoDatabase = mongoClient.getDatabase(dbName);
    Assert.assertNotNull(mongoDatabase);
    clearDatabase(mongoDatabase);
  }

  @SuppressWarnings("unchecked")
  @Given("^the database instance is populated with HealthCheck data from \"([^\"]*)\"$")
  public void the_database_instance_is_populated_with_HealthCheck_data(String dataFileName) throws Throwable {
    String fileString = new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(String.format("data/%s", dataFileName)).toURI())));
    Map<String, List<Map<String, Object>>> healthCheckData = objectMapper.readValue(fileString, typeReference);
    COLLECTIONS_LIST.forEach(collectionName -> {
      MongoCollection collection = mongoDatabase.getCollection(collectionName);
      healthCheckData.get(collectionName).forEach(collectionEntry ->
        collection.insertOne(new Document(collectionEntry))
      );
    });
  }

  @Then("^the \"([^\"]*)\" collection of the database will have the following entries:$")
  public void the_collection_of_the_database_will_have_the_following_entries(String collectionName, List<Map<String, Object>> expectedEntriesList) throws Throwable {
    // Write code here that turns the phrase above into concrete actions
    // For automatic transformation, change DataTable to one of
    // List<YourType>, List<List<E>>, List<Map<K,V>> or Map<K,V>.
    // E,K,V must be a scalar (String, Integer, Date, enum etc)
    MongoCollection collection = mongoDatabase.getCollection(collectionName);
    Assert.assertNotNull(String.format("Database collection '%s' not found.", collectionName), collection);
    for (Map<String, Object> expectedRow : expectedEntriesList) {
      Bson[] givenFilters = expectedRow.entrySet().stream()
          .map(entry -> Filters.eq(entry.getKey(), entry.getValue()))
          .collect(Collectors.toList())
          .toArray(new Bson[0]);
      Collection resultsList = collection.find(
          Filters.and(
              givenFilters
          )
      ).into(new ArrayList());
      Assert.assertThat(String.format("Should only find one record for '%s'", expectedRow), resultsList.size(), Matchers.is(1));
    }
  }

  private void clearDatabase(MongoDatabase database) {
    MongoCollection offsetsCollection = database.getCollection("_offsets");
    COLLECTIONS_LIST.forEach(collectionName -> {
      database.getCollection(collectionName).deleteMany(new Document());
      offsetsCollection.findOneAndUpdate(
          Filters.eq("topic", collectionName),
          new Document("$set", new Document("offset", 0))
      );
    });
  }
}
