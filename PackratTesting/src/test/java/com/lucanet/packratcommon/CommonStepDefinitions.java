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
import org.bson.Document;
import org.junit.Assert;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
