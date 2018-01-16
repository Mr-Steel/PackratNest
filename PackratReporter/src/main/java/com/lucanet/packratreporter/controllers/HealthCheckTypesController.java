package com.lucanet.packratreporter.controllers;

import com.lucanet.packratcommon.aspects.LogExecution;
import com.lucanet.packratcommon.db.DatabaseConnection;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
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
  @ApiOperation(
      value = "Get all HealthCheck types",
      responseContainer = "List"
  )
  @ApiResponses(value = {
      @ApiResponse(code = HttpServletResponse.SC_OK, message = "HealthCheck types obtained")
  })
  @LogExecution
  @RequestMapping(value = "/types", method = RequestMethod.GET, produces = "application/json")
  public List<String> getHealthCheckTypes(HttpServletResponse servletResponse) {
    List<String> topicsList;
    try {
      topicsList = databaseConnection.getHealthCheckTypes();
      servletResponse.setStatus(HttpServletResponse.SC_OK);
    } catch (Exception e) {
      logger.error("Error occurred in getHealthCheckTypes:", e);
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
  @ApiOperation(
      value = "Get system UUIDs that have entries for a HealthCheck type",
      responseContainer = "List"
  )
  @ApiResponses(value = {
      @ApiResponse(code = HttpServletResponse.SC_OK, message = "List of SystemUUIDs obtained for the supplied HealthCheck type"),
      @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "The supplied HealthCheck type does not exist")
  })
  @LogExecution
  @RequestMapping(value = "/{healthCheckType}/systemuuids", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String> getDistinctSystems(
      HttpServletResponse servletResponse,
      @ApiParam(
          value = "HealthCheck Type",
          required = true
      )
      @PathVariable("healthCheckType") String healthCheckType
  ) {
    List<String> distinctSystemsList = new ArrayList<>();
    try {
      distinctSystemsList.addAll(databaseConnection.getSystemsInHealthCheckType(healthCheckType));
      servletResponse.setStatus(HttpServletResponse.SC_OK);
    } catch (IllegalArgumentException iae) {
      logger.warn("Illegal argument in SystemUUIDs query: {}", iae.getMessage());
      servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error occurred in getDistinctSystems:", e);
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
  @ApiOperation(
      value = "Get session timestamps of a system UUID that has HealthCheck entries",
      responseContainer = "List"
  )
  @ApiResponses(value = {
      @ApiResponse(code = HttpServletResponse.SC_OK, message = "List of session timestamps obtained for the supplied HealthCheck type and system UUID"),
      @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "The supplied HealthCheck type does not exist")
  })
  @LogExecution
  @RequestMapping(value = "/{healthCheckType}/sessions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Long> getSessionTimestamps(
      HttpServletResponse servletResponse,
      @ApiParam(
          value = "HealthCheck Type",
          required = true
      )
      @PathVariable("healthCheckType") String healthCheckType,
      @ApiParam(
          value = "System UUID",
          required = true
      )
      @RequestParam("systemUUID") String systemUUID
  ) {
    List<Long> sessionTimestamps = new ArrayList<>();
    try {
      sessionTimestamps.addAll(databaseConnection.getSessionTimestamps(healthCheckType, systemUUID));
    } catch (IllegalArgumentException iae) {
      logger.warn("Illegal argument in Session Timestamps query: {}", iae.getMessage());
      servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error occurred in getSessionTimestamps:", e);
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
  @ApiOperation(
      value = "Get HealthCheck entries of a session for a system UUID",
      responseContainer = "List"
  )
  @ApiResponses(value = {
      @ApiResponse(code = HttpServletResponse.SC_OK, message = "List of HealthChecks obtained for the supplied HealthCheck type, " +
          "system UUID, and session timestamp"),
      @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "The supplied HealthCheck type does not exist")
  })
  @LogExecution
  @RequestMapping(value = "/{healthCheckType}/entries", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Map<String, Object>> getSessionHealthChecks(
      HttpServletResponse servletResponse,
      @ApiParam(
          value = "HealthCheck Type",
          required = true
      )
      @PathVariable("healthCheckType") String healthCheckType,
      @ApiParam(
          value = "System UUID",
          required = true
      )
      @RequestParam("systemUUID") String systemUUID,
      @ApiParam(
          value = "Session Timestamp",
          required = true
      )
      @RequestParam("sessionTimestamp") Long sessionTimestamp
  ) {
    List<Map<String, Object>> sessionHealthChecks = new ArrayList<>();
    try {
      sessionHealthChecks.addAll(databaseConnection.getSessionHealthChecks(healthCheckType, systemUUID, sessionTimestamp));
    } catch (IllegalArgumentException iae) {
      logger.warn("Illegal argument in Session HealthChecks query: {}", iae.getMessage());
      servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error occurred in getSessionHealthChecks:", e);
      servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return sessionHealthChecks;
  }

  @PreDestroy
  public void shutdown() {
    databaseConnection.shutdown();
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
