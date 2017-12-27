package com.lucanet.packratcommon.db;

import com.lucanet.packratcommon.model.HealthCheckHeader;
import com.lucanet.packratcommon.model.HealthCheckRecord;
import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
class MongoDatabaseConnection implements DatabaseConnection {

  private final Logger        logger;
  private final MongoDatabase healthCheckDB;

  public MongoDatabaseConnection(
      @Value("${packrat.persister.url}") String dbURL,
      @Value("${packrat.persister.port}") int dbPort,
      @Value("${packrat.persister.username}") String username,
      @Value("${packrat.persister.password}") String password
  ) {
    logger = LoggerFactory.getLogger(MongoDatabaseConnection.class);
    logger.info("Building CouchDB connection to {}:{}@{}:{}", username, password, dbURL, dbPort);
    MongoClientOptions.Builder clientOptionsBuilder = new MongoClientOptions.Builder();
    MongoClient mongoClient = new MongoClient(
        new ServerAddress(dbURL, dbPort),
        MongoCredential.createCredential(username, "packrat_healthcheck", password.toCharArray()),
        clientOptionsBuilder.build()
    );
    this.healthCheckDB = mongoClient.getDatabase("packrat_healthcheck");
  }

  @Override
  public <T> void persistRecord(String topicName, HealthCheckHeader healthCheckHeader, T record) throws IllegalArgumentException {
    HealthCheckRecord healthCheckRecord = new HealthCheckRecord<>(healthCheckHeader, record);
    try {
      healthCheckDB.getCollection(topicName, HealthCheckRecord.class).insertOne(healthCheckRecord);
    } catch (MongoWriteException mwe) {
      if (mwe.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
        logger.warn("Cannot write message {} - entry already exists with this key for topic '{}'", healthCheckHeader, topicName);
      } else {
        logger.error("Error writing message {} to topic '{}': {}", healthCheckHeader, topicName, mwe.getMessage());
      }
    }
  }

  @Override
  public long getOffset(String topicName, int partition) throws IllegalArgumentException {
    MongoCollection<Document> collection = healthCheckDB.getCollection(OFFSETS_COLLECTION_NAME, Document.class);
    Document offsetDoc = collection.find(
        Filters.and(
            Filters.eq(OFFSETS_TOPIC_KEY, topicName),
            Filters.eq(OFFSETS_PARTITION_KEY, partition)
        )
    ).first();
    if (offsetDoc != null) {
      return offsetDoc.getLong(OFFSETS_OFFSET_KEY);
    } else {
      //Place the base offset value for this topic/partition in the database to establish
      //an entry
      offsetDoc = new Document()
          .append(OFFSETS_TOPIC_KEY, topicName)
          .append(OFFSETS_PARTITION_KEY, partition)
          .append(OFFSETS_OFFSET_KEY, 0L);
      collection.insertOne(offsetDoc);
      return 0L;
    }
  }

  @Override
  public void updateOffset(String topicName, int partition, long newOffset) throws IllegalArgumentException {
    Document newOffsetDoc = new Document()
        .append(OFFSETS_TOPIC_KEY, topicName)
        .append(OFFSETS_PARTITION_KEY, partition)
        .append(OFFSETS_OFFSET_KEY, newOffset);

    //Only update the offset if it is the highest value possible. FindOneAndReplace is an atomic
    //update action, which will maintain thread safety
    Document updatedDoc = healthCheckDB.getCollection(OFFSETS_COLLECTION_NAME, Document.class)
        .findOneAndReplace(
            Filters.and(
                Filters.eq(OFFSETS_TOPIC_KEY, topicName),
                Filters.eq(OFFSETS_PARTITION_KEY, partition),
                Filters.lt(OFFSETS_OFFSET_KEY, newOffset)
            ),
            newOffsetDoc
        );
    if (updatedDoc != null) {
      logger.debug("Set new offset to {} for topic '{}' partition {}", newOffset, topicName, partition);
    }
  }

  @Override
  public List<String> getTopics() {
    return healthCheckDB.listCollections()
        .filter(Filters.ne("name", OFFSETS_COLLECTION_NAME))
        .map(document -> document.getString("name"))
        .into(new ArrayList<>());
  }

  @Override
  public List<String> getSystemsInTopic(String topicName) throws IllegalArgumentException {
    if (getTopics().contains(topicName)) {
      return healthCheckDB.getCollection(topicName, HealthCheckRecord.class)
          .distinct(HealthCheckRecord.SYSTEM_UUID, String.class)
          .into(new ArrayList<>());
    } else {
      throw new IllegalArgumentException(String.format("Topic '%s' doesn't exist", topicName));
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Long> getSessionTimestamps(String topicName, String systemUUID) throws IllegalArgumentException {
    if (getTopics().contains(topicName)) {
      AggregateIterable<Document> iterable = healthCheckDB.getCollection(topicName)
          .aggregate(
              Arrays.asList(
                  Aggregates.match(Filters.eq(HealthCheckRecord.SYSTEM_UUID, systemUUID)),
                  Aggregates.group(
                      String.format("$%s", HealthCheckRecord.SYSTEM_UUID),
                      Accumulators.addToSet(
                          HealthCheckRecord.SESSION_TIMESTAMP,
                          String.format("$%s", HealthCheckRecord.SESSION_TIMESTAMP)
                      )
                  )
              )
          );
      return iterable.first().get(HealthCheckRecord.SESSION_TIMESTAMP, List.class);
    } else {
      throw new IllegalArgumentException(String.format("Topic '%s' doesn't exist", topicName));
    }
  }

  @Override
  public List<Map<String, Object>> getSessionHealthChecks(String topicName, String systemUUID, Long sessionTimestamp) throws IllegalArgumentException {
    if (getTopics().contains(topicName)) {
      return healthCheckDB.getCollection(topicName, Document.class)
          .find(Filters.and(
              Filters.eq(HealthCheckRecord.SYSTEM_UUID, systemUUID),
              Filters.eq(HealthCheckRecord.SESSION_TIMESTAMP, sessionTimestamp)
          ), Document.class)
          .into(new ArrayList<>());
    } else {
      throw new IllegalArgumentException(String.format("Topic '%s' doesn't exist", topicName));
    }
  }

  @Override
  public Map<String, List<String>> getSerialIDS() {
    return getTopics().stream()
        .collect(Collectors.toMap(
            topic -> topic,
            topic -> healthCheckDB.getCollection(topic)
                .distinct(HealthCheckRecord.SERIAL_ID, String.class)
                .into(new ArrayList<>())
        ));
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, List<String>> getSystemsForSerialID(String serialID) {
    return getTopics().stream()
        .collect(Collectors.toMap(
            topic -> topic,
            topic -> healthCheckDB.getCollection(topic)
                .aggregate(
                    Arrays.asList(
                        Aggregates.match(Filters.eq(HealthCheckRecord.SERIAL_ID, serialID)),
                        Aggregates.group(String.format("$%s", HealthCheckRecord.SERIAL_ID), Accumulators.addToSet(HealthCheckRecord.SYSTEM_UUID, String.format("$%s", HealthCheckRecord.SYSTEM_UUID)))
                    )
                ).first()
                .get(HealthCheckRecord.SYSTEM_UUID, List.class)
        ));
  }
}
