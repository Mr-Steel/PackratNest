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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DatabaseConnection} utilizing MongoDB.
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
@Service
class MongoDatabaseConnection implements DatabaseConnection {
  // =========================== Class Variables ===========================79
  /**
   * Name of the Offsets collection ("_offsets") in the MongoDB database
   */
  private static final String OFFSETS_COLLECTION_NAME = "_offsets";
  /**
   * "topic" field key for usage in the Offsets collection of the MongoDB database
   */
  private static final String OFFSETS_TOPIC_KEY = "topic";
  /**
   * "partition" field key for usage in the Offsets collection of the MongoDB database
   */
  private static final String OFFSETS_PARTITION_KEY = "partition";
  /**
   * "offset" field key for usage in the Offsets collection of the MongoDB database
   */
  private static final String OFFSETS_OFFSET_KEY = "offset";

  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  /**
   * The logger for the MongoDatabaseConnection instance.
   */
  private final Logger logger;
  /**
   * The MongoDB client connection representation.
   */
  private final MongoClient mongoClient;
  /**
   * The MongoDB database representation.
   */
  private final MongoDatabase healthCheckDB;

  // ============================  Constructors  ===========================79
  /**
   * Database connection constructor.
   * @param dbURL MongoDB instance's URL.
   * @param dbPort MongoDB instance's port.
   * @param username Username for logging in to interact with the MongoDB instance.
   * @param password Password for logging in to interact with the MongoDB instance.
   */
  public MongoDatabaseConnection(
      @Value("${packrat.db.url}") String dbURL,
      @Value("${packrat.db.port}") int dbPort,
      @Value("${packrat.db.dbname}") String dbName,
      @Value("${packrat.db.username}") String username,
      @Value("${packrat.db.password}") String password
  ) {
    logger = LoggerFactory.getLogger(MongoDatabaseConnection.class);
    logger.info("Building CouchDB connection to {}:{}@{}:{}", username, password, dbURL, dbPort);
    MongoClientOptions.Builder clientOptionsBuilder = new MongoClientOptions.Builder();
    mongoClient = new MongoClient(
        new ServerAddress(dbURL, dbPort),
        MongoCredential.createCredential(username, dbName, password.toCharArray()),
        clientOptionsBuilder.build()
    );
    this.healthCheckDB = mongoClient.getDatabase(dbName);
  }

  // ============================ Public Methods ===========================79
  /**
   * Persist a HealthCheck record.
   * @param healthCheckType The type of the HealthCheck record.
   * @param healthCheckHeader The HealthCheck's metadata.
   * @param record The HealthCheck record.
   * @throws IllegalArgumentException Signifies that the HealthCheck type does not exist in the database.
   */
  @Override
  public <T> void persistRecord(String healthCheckType, HealthCheckHeader healthCheckHeader, T record) throws IllegalArgumentException {
    HealthCheckRecord healthCheckRecord = new HealthCheckRecord<>(healthCheckHeader, record);
    try {
      healthCheckDB.getCollection(healthCheckType, HealthCheckRecord.class).insertOne(healthCheckRecord);
    } catch (MongoWriteException mwe) {
      if (mwe.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
        logger.warn("Cannot write message {} - entry already exists with this key for topic '{}'", healthCheckHeader, healthCheckType);
      } else {
        logger.error("Error writing message {} to topic '{}': {}", healthCheckHeader, healthCheckType, mwe.getMessage());
      }
    }
  }

  /**
   * Get the message offset for the specified HealthCheck type and message partition.
   * @param healthCheckType The specified HealthCheck type.
   * @param partition The message partition for the HealthCheck type.
   * @return The message offset.
   * @throws IllegalArgumentException Signifies that the HealthCheck type does not exist in the database.
   */
  @Override
  public long getOffset(String healthCheckType, int partition) throws IllegalArgumentException {
    MongoCollection<Document> collection = healthCheckDB.getCollection(OFFSETS_COLLECTION_NAME, Document.class);
    Document offsetDoc = collection.find(
        Filters.and(
            Filters.eq(OFFSETS_TOPIC_KEY, healthCheckType),
            Filters.eq(OFFSETS_PARTITION_KEY, partition)
        )
    ).first();
    if (offsetDoc != null) {
      return offsetDoc.getLong(OFFSETS_OFFSET_KEY);
    } else {
      //Place the base offset value for this topic/partition in the database to establish
      //an entry
      offsetDoc = new Document()
          .append(OFFSETS_TOPIC_KEY, healthCheckType)
          .append(OFFSETS_PARTITION_KEY, partition)
          .append(OFFSETS_OFFSET_KEY, 0L);
      collection.insertOne(offsetDoc);
      return 0L;
    }
  }

  /**
   * Set the new message offset for the specified HealthCheck type and message partition.
   * @param healthCheckType The specified HealthCheck type.
   * @param partition The message partition for the HealthCheck type.
   * @param newOffset The new message offset.
   * @throws IllegalArgumentException Signifies that the HealthCheck type does not exist in the database.
   */
  @Override
  public void updateOffset(String healthCheckType, int partition, long newOffset) throws IllegalArgumentException {
    Document newOffsetDoc = new Document()
        .append(OFFSETS_TOPIC_KEY, healthCheckType)
        .append(OFFSETS_PARTITION_KEY, partition)
        .append(OFFSETS_OFFSET_KEY, newOffset);

    //Only update the offset if it is the highest value possible. FindOneAndReplace is an atomic
    //update action, which will prevent race conditions with the MongoDB being pinged by multiple
    //requests simultaneously
    Document updatedDoc = healthCheckDB.getCollection(OFFSETS_COLLECTION_NAME, Document.class)
        .findOneAndReplace(
            Filters.and(
                Filters.eq(OFFSETS_TOPIC_KEY, healthCheckType),
                Filters.eq(OFFSETS_PARTITION_KEY, partition),
                Filters.lt(OFFSETS_OFFSET_KEY, newOffset)
            ),
            newOffsetDoc
        );
    if (updatedDoc != null) {
      logger.debug("Set new offset to {} for topic '{}' partition {}", newOffset, healthCheckType, partition);
    }
  }

  /**
   * Request a list of HealthCheck types that the database persists. This will pull the names of all collections in the database.
   * that don't match the value represented by {@link #OFFSETS_COLLECTION_NAME}.
   * @return The list of persistable HealthCheck types.
   */
  @Override
  public List<String> getHealthCheckTypes() {
    return healthCheckDB.listCollections()
        .filter(Filters.ne("name", OFFSETS_COLLECTION_NAME))
        .map(document -> document.getString("name"))
        .into(new ArrayList<>());
  }

  /**
   * Request a list of all computer instances that have HealthCheck records stored for the specified HealthCheck type.
   * @param healthCheckType The specified HealthCheck type.
   * @return The list of relevant computers (in the form of UUID entities).
   * @throws IllegalArgumentException Signifies that the HealthCheck type does not exist in the database.
   */
  @Override
  public List<String> getSystemsInHealthCheckType(String healthCheckType) throws IllegalArgumentException {
    if (getHealthCheckTypes().contains(healthCheckType)) {
      return healthCheckDB.getCollection(healthCheckType, HealthCheckRecord.class)
          .distinct(HealthCheckRecord.SYSTEM_UUID, String.class)
          .into(new ArrayList<>());
    } else {
      throw new IllegalArgumentException(String.format("Topic '%s' doesn't exist", healthCheckType));
    }
  }

  /**
   * Request a list of all sessions in which a computer has HealthCheck records stored for the specified HealthCheck type.
   * @param healthCheckType The specified HealthCheck type.
   * @param systemUUID The specified computer (in the form of a UUID entity).
   * @return The list of sessions (in the form of timestamps representing seconds elapsed since the UNIX epoch).
   * @throws IllegalArgumentException Signifies that the HealthCheck type does not exist in the database.
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<Long> getSessionTimestamps(String healthCheckType, String systemUUID) throws IllegalArgumentException {
    if (getHealthCheckTypes().contains(healthCheckType)) {
      //Aggregate timestamp data from database
      List<Number> timestampsList = Optional.ofNullable(
          healthCheckDB.getCollection(healthCheckType)
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
              ).first()
      ).map(doc ->
          doc.get(HealthCheckRecord.SESSION_TIMESTAMP, List.class)
      ).orElse(new ArrayList());
      //Force conversion of timestamp values to Long, as they may come out of the
      //database as an Integer type
      return timestampsList.stream().map(Number::longValue).collect(Collectors.toList());
    } else {
      throw new IllegalArgumentException(String.format("Topic '%s' doesn't exist", healthCheckType));
    }
  }

  /**
   * Request a list of all HealthCheck records for a specified computer's session correlating to a HealthCheck type.
   * @param healthCheckType The specified HealthCheck type.
   * @param systemUUID The specified computer (in the form of a UUID entity).
   * @param sessionTimestamp The specified session (in the form of a timestamp representing seconds elapsed since the UNIX epoch).
   * @return The list of all HealthCheck records for the computer that occurred in the specified session.
   * @throws IllegalArgumentException Signifies that the HealthCheck type does not exist in the database.
   */
  @Override
  public List<Map<String, Object>> getSessionHealthChecks(String healthCheckType, String systemUUID, Long sessionTimestamp) throws IllegalArgumentException {
    if (getHealthCheckTypes().contains(healthCheckType)) {
      return healthCheckDB.getCollection(healthCheckType, Document.class)
          .find(Filters.and(
              Filters.eq(HealthCheckRecord.SYSTEM_UUID, systemUUID),
              Filters.eq(HealthCheckRecord.SESSION_TIMESTAMP, sessionTimestamp)
          ), Document.class)
          .into(new ArrayList<>());
    } else {
      throw new IllegalArgumentException(String.format("Topic '%s' doesn't exist", healthCheckType));
    }
  }

  /**
   * Request a map of all computer groups that have records in each HealthCheck type.
   * @return The map of all computer groups present (in the form of a serial ID for each computer group).
   */
  @Override
  public Map<String, List<String>> getSerialIds() {
    return getHealthCheckTypes().stream()
        .collect(Collectors.toMap(
            topic -> topic,
            topic -> healthCheckDB.getCollection(topic)
                .distinct(HealthCheckRecord.SERIAL_ID, String.class)
                .into(new ArrayList<>())
        ));
  }

  /**
   * Request a map of all computers that have records in each HealthCheck type for a specified computer group.
   * @param serialId The specified computer group (in the form of a serial ID).
   * @return The map of all computers (in the form of UUID entities) in a computer group that have records stored for each HealthCheck type.
   */
  @Override
  @SuppressWarnings("unchecked")
  public Map<String, List<String>> getSystemsForSerialID(String serialId) {
    return getHealthCheckTypes().stream()
        .collect(Collectors.toMap(
            topic -> topic,
            topic -> {
              Optional<Document> queryResults = Optional.ofNullable(
                  healthCheckDB.getCollection(topic)
                      .aggregate(
                          Arrays.asList(
                              Aggregates.match(Filters.eq(HealthCheckRecord.SERIAL_ID, serialId)),
                              Aggregates.group(
                                  String.format("$%s", HealthCheckRecord.SERIAL_ID),
                                  Accumulators.addToSet(HealthCheckRecord.SYSTEM_UUID, String.format("$%s", HealthCheckRecord.SYSTEM_UUID))
                              )
                          )
                      ).first());
              return queryResults.map(doc -> doc.get(HealthCheckRecord.SYSTEM_UUID, List.class)).orElse(new ArrayList());
            }
        ));
  }

  /**
   * Shut down the MongoDB client connection
   */
  @Override
  public void shutdown() {
    mongoClient.close();
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
