package com.lucanet.packratreporter.controllers;

import com.lucanet.packratcommon.aspects.LogExecution;
import com.lucanet.packratcommon.db.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/topics")
public class TopicsController {

  private final Logger logger;
  private final DatabaseConnection databaseConnection;

  public TopicsController(DatabaseConnection databaseConnection) {
    this.logger = LoggerFactory.getLogger(TopicsController.class);
    this.databaseConnection = databaseConnection;
  }

  @LogExecution
  @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
  public List<String> getTopics(HttpServletResponse response) {
    List<String> topicsList;
    try {
      topicsList = databaseConnection.getTopics();
      response.setStatus(HttpServletResponse.SC_OK);
    } catch (Exception e) {
      logger.error("Error occurred in getTopics: {}", e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      topicsList = new ArrayList<>();
    }
    return topicsList;
  }

  @LogExecution
  @RequestMapping(value = "/{topicName}/systemuuids", method = RequestMethod.GET, produces = "application/json")
  public List<String> getDistinctSystems(HttpServletResponse response, @PathVariable("topicName") String topicName) {
    List<String> distinctSystemsList = new ArrayList<>();
    try {
      distinctSystemsList.addAll(databaseConnection.getSystemsInTopic(topicName));
      response.setStatus(HttpServletResponse.SC_OK);
    } catch (IllegalArgumentException iae) {
      logger.warn("Illegal argument in SystemUUIDs query: {}", iae.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error occurred in getDistinctSystems: {} ({})", e.getMessage(), e.getClass());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return distinctSystemsList;
  }

  @LogExecution
  @RequestMapping(value = "/{topicName}/sessions", method = RequestMethod.GET, produces = "application/json")
  public List<Long> getSessionTimestamps(
      HttpServletResponse response,
      @PathVariable("topicName") String topicName,
      @RequestParam("systemUUID") String systemUUID
  ) {
    List<Long> sessionTimestamps = new ArrayList<>();
    try {
      sessionTimestamps.addAll(databaseConnection.getSessionTimestamps(topicName, systemUUID));
    } catch (IllegalArgumentException iae) {
      logger.warn("Illegal argument in Session Timestamps query: {}", iae.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error occurred in getSessionTimestamps: {} ({})", e.getMessage(), e.getClass());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return sessionTimestamps;
  }

  @LogExecution
  @RequestMapping(value = "/{topicName}/healthchecks", method = RequestMethod.GET, produces = "application/json")
  public List<Map<String, Object>> getSessionHealthChecks(
      HttpServletResponse response,
      @PathVariable("topicName") String topicName,
      @RequestParam("systemUUID") String systemUUID,
      @RequestParam("sessionTimestamp") Long sessionTimestamp
  ) {
    List<Map<String, Object>> sessionHealthChecks = new ArrayList<>();
    try {
      sessionHealthChecks.addAll(databaseConnection.getSessionHealthChecks(topicName, systemUUID, sessionTimestamp));
    } catch (IllegalArgumentException iae) {
      logger.warn("Illegal argument in Session HealthChecks query: {}", iae.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error occurred in getSessionHealthChecks: {} ({})", e.getMessage(), e.getClass());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return sessionHealthChecks;
  }

}
