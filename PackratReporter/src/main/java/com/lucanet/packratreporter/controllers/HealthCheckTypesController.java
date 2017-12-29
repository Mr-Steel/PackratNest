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

/**
 * REST controller for HealthCheck-related requests.
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
@RestController
@RequestMapping("/healthchecks")
public class HealthCheckTypesController {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  /**
   * The logger for the HealthCheckTypesController.
   */
  private final Logger logger;
  /**
   * The database persistence object.
   */
  private final DatabaseConnection databaseConnection;

  // ============================  Constructors  ===========================79
  /**
   * Controller constructor.
   * @param databaseConnection The database persistence object.
   */
  public HealthCheckTypesController(DatabaseConnection databaseConnection) {
    this.logger = LoggerFactory.getLogger(HealthCheckTypesController.class);
    this.databaseConnection = databaseConnection;
  }

  // ============================ Public Methods ===========================79
  /**
   * Get list of HealthCheck types that the data persistence layer contains.
   * @param servletResponse Response object used to set the HTTP response code.
   * @return List of HealthCheck types.
   */
  @LogExecution
  @RequestMapping(value = "/types", method = RequestMethod.GET, produces = "application/json")
  public List<String> getHealthCheckTypes(HttpServletResponse servletResponse) {
    List<String> topicsList;
    try {
      topicsList = databaseConnection.getHealthCheckTypes();
      servletResponse.setStatus(HttpServletResponse.SC_OK);
    } catch (Exception e) {
      logger.error("Error occurred in getHealthCheckTypes: {}", e.getMessage());
      servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      topicsList = new ArrayList<>();
    }
    return topicsList;
  }

  /**
   * Get list of all computer instances that have HealthCheck records stored for the specified HealthCheck type.
   * @param servletResponse Response object used to set the HTTP response code.
   * @param healthCheckType The specified HealthCheck type.
   * @return The list of relevant computers (in the form of UUID entities).
   */
  @LogExecution
  @RequestMapping(value = "/{healthCheckType}/systemuuids", method = RequestMethod.GET, produces = "application/json")
  public List<String> getDistinctSystems(HttpServletResponse servletResponse, @PathVariable("healthCheckType") String healthCheckType) {
    List<String> distinctSystemsList = new ArrayList<>();
    try {
      distinctSystemsList.addAll(databaseConnection.getSystemsInHealthCheckType(healthCheckType));
      servletResponse.setStatus(HttpServletResponse.SC_OK);
    } catch (IllegalArgumentException iae) {
      logger.warn("Illegal argument in SystemUUIDs query: {}", iae.getMessage());
      servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error occurred in getDistinctSystems: {} ({})", e.getMessage(), e.getClass());
      servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return distinctSystemsList;
  }

  /**
   * Get list of all sessions in which a computer has HealthCheck records stored for the specified HealthCheck type.
   * @param servletResponse Response object used to set the HTTP response code.
   * @param healthCheckType The specified HealthCheck type.
   * @param systemUUID The specified computer (in the form of a UUID entity).
   * @return The list of sessions (in the form of timestamps representing seconds elapsed since the UNIX epoch).
   */
  @LogExecution
  @RequestMapping(value = "/{healthCheckType}/sessions", method = RequestMethod.GET, produces = "application/json")
  public List<Long> getSessionTimestamps(
      HttpServletResponse servletResponse,
      @PathVariable("healthCheckType") String healthCheckType,
      @RequestParam("systemUUID") String systemUUID
  ) {
    List<Long> sessionTimestamps = new ArrayList<>();
    try {
      sessionTimestamps.addAll(databaseConnection.getSessionTimestamps(healthCheckType, systemUUID));
    } catch (IllegalArgumentException iae) {
      logger.warn("Illegal argument in Session Timestamps query: {}", iae.getMessage());
      servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error occurred in getSessionTimestamps: {} ({})", e.getMessage(), e.getClass());
      servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return sessionTimestamps;
  }

  /**
   * Get list of all HealthCheck records for a specified computer's session correlating to a HealthCheck type.
   * @param servletResponse Response object used to set the HTTP response code.
   * @param healthCheckType The specified HealthCheck type.
   * @param systemUUID The specified computer (in the form of a UUID entity).
   * @param sessionTimestamp The specified session (in the form of a timestamp representing seconds elapsed since the UNIX epoch).
   * @return The list of all HealthCheck records for the computer that occurred in the specified session.
   */
  @LogExecution
  @RequestMapping(value = "/{healthCheckType}/healthchecks", method = RequestMethod.GET, produces = "application/json")
  public List<Map<String, Object>> getSessionHealthChecks(
      HttpServletResponse servletResponse,
      @PathVariable("healthCheckType") String healthCheckType,
      @RequestParam("systemUUID") String systemUUID,
      @RequestParam("sessionTimestamp") Long sessionTimestamp
  ) {
    List<Map<String, Object>> sessionHealthChecks = new ArrayList<>();
    try {
      sessionHealthChecks.addAll(databaseConnection.getSessionHealthChecks(healthCheckType, systemUUID, sessionTimestamp));
    } catch (IllegalArgumentException iae) {
      logger.warn("Illegal argument in Session HealthChecks query: {}", iae.getMessage());
      servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error occurred in getSessionHealthChecks: {} ({})", e.getMessage(), e.getClass());
      servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return sessionHealthChecks;
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}