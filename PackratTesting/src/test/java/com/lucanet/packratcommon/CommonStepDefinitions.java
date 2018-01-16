package com.lucanet.packratcommon;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucanet.packratcommon.db.MongoDatabaseConnection;
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
import cucumber.api.java.en.When;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

public class CommonStepDefinitions {

  private static final List<String> COLLECTIONS_LIST = Arrays.asList(
      "DynamicSystemStats",
      "StaticSystemStats",
      "SummaDatabase",
      "TransactionStats"
  );

  private static final Map<String, Function> TYPE_TRANSFORMER_MAP;
  static {
    TYPE_TRANSFORMER_MAP = new HashMap<>();
    TYPE_TRANSFORMER_MAP.put("STRING", obj -> obj);
    TYPE_TRANSFORMER_MAP.put("INTEGER", obj -> Integer.valueOf(obj.toString()));
    TYPE_TRANSFORMER_MAP.put("LONG", obj -> Long.valueOf(obj.toString()));
  }

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

  @When("^I wait (\\d+) seconds$")
  public void i_wait_seconds(int sleepSecondsCount) throws Throwable {
    Thread.sleep((long) sleepSecondsCount * 1000L);
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
    MongoCollection offsetsCollection = mongoDatabase.getCollection(MongoDatabaseConnection.OFFSETS_COLLECTION_NAME);
    healthCheckData.get(MongoDatabaseConnection.OFFSETS_COLLECTION_NAME).forEach(collectionEntry -> {
      Document offsetDoc = new Document();
      offsetDoc.put(
          MongoDatabaseConnection.OFFSETS_TOPIC_KEY,
          collectionEntry.get(MongoDatabaseConnection.OFFSETS_TOPIC_KEY)
      );
      offsetDoc.put(
          MongoDatabaseConnection.OFFSETS_PARTITION_KEY,
          Integer.valueOf(collectionEntry.get(MongoDatabaseConnection.OFFSETS_PARTITION_KEY).toString())
      );
      offsetDoc.put(
          MongoDatabaseConnection.OFFSETS_OFFSET_KEY,
          Long.valueOf(collectionEntry.get(MongoDatabaseConnection.OFFSETS_OFFSET_KEY).toString())
      );
      offsetsCollection.insertOne(offsetDoc);
    });
  }

  @SuppressWarnings("unchecked")
  @Then("^the \"([^\"]*)\" collection of the database will have an entry with the following attributes:$")
  public void the_collection_of_the_database_will_have_an_entry_with_the_following_attributes(
      String collectionName,
      List<Map<String, Object>> expectedAttributesList
  ) throws Throwable {
    MongoCollection collection = mongoDatabase.getCollection(collectionName);
    Assert.assertNotNull(String.format("Database collection '%s' not found.", collectionName), collection);
    List<Bson> filtersList = new ArrayList<>();
    for (Map<String, Object> expectedAttribute : expectedAttributesList) {
      String attributeType = ((String) expectedAttribute.get("Type")).toUpperCase();
      if (TYPE_TRANSFORMER_MAP.containsKey(attributeType)) {
        filtersList.add(
            Filters.eq(
                expectedAttribute.get("Name").toString(),
                TYPE_TRANSFORMER_MAP.get(attributeType).apply(expectedAttribute.get("Value"))
            )
        );
      } else {
        throw new IllegalArgumentException(String.format("Type '%s' not supported - must be one of the following: %s", attributeType, TYPE_TRANSFORMER_MAP.keySet()));
      }
    }
    Collection resultsList = collection.find(Filters.and(filtersList)).into(new ArrayList());
    Assert.assertThat(String.format("Should only find one record for '%s' with attributes %s", collectionName, expectedAttributesList), resultsList.size(), Matchers.is(1));
  }

  @Then("^the \"([^\"]*)\" collection will be empty$")
  public void the_collection_will_be_empty(String collectionName) throws Throwable {
    MongoCollection collection = mongoDatabase.getCollection(collectionName);
    Assert.assertNotNull(String.format("Database collection '%s' not found.", collectionName), collection);
    Assert.assertThat(String.format("Database collection '%s' should be empty.", collectionName), collection.count(), Matchers.is(0L));
  }

  private void clearDatabase(MongoDatabase database) {
    database.getCollection(MongoDatabaseConnection.OFFSETS_COLLECTION_NAME)
        .deleteMany(new Document());
    COLLECTIONS_LIST.forEach(collectionName ->
      database.getCollection(collectionName).deleteMany(new Document())
    );
  }
}
