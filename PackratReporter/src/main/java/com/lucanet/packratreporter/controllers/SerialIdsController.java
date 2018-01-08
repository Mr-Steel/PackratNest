package com.lucanet.packratreporter.controllers;

import com.lucanet.packratcommon.aspects.LogExecution;
import com.lucanet.packratcommon.db.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Serial Id-related requests.
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
@RestController
@RequestMapping("/serialids")
public class SerialIdsController {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  /**
   * The logger for the SerialIdsController.
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
  public SerialIdsController(DatabaseConnection databaseConnection) {
    this.logger = LoggerFactory.getLogger(SerialIdsController.class);
    this.databaseConnection = databaseConnection;
  }

  // ============================ Public Methods ===========================79
  /**
   * Get map of all computer groups that have records in each HealthCheck type.
   * @param servletResponse Response object used to set the HTTP response code.
   * @return The map of all computer groups present (in the form of a serial ID for each computer group).
   */
  @LogExecution
  @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
  public Map<String, List<String>> getSerialIDS(HttpServletResponse servletResponse) {
    Map<String, List<String>> serialIDS = new HashMap<>();
    try {
      serialIDS.putAll(databaseConnection.getSerialIds());
    } catch (Exception e) {
      logger.error("Error occurred in getSerialIds:", e);
      servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return serialIDS;
  }

  /**
   * Get map of all computers that have records in each HealthCheck type for a specified computer group.
   * @param servletResponse Response object used to set the HTTP response code.
   * @param serialId The specified computer group (in the form of a serial ID).
   * @return The map of all computers (in the form of UUID entities) in a computer group that have records stored for each HealthCheck type.
   */
  @LogExecution
  @RequestMapping(value = "/{serialId}/systems")
  public Map<String, List<String>> getSystemsForSerialId(HttpServletResponse servletResponse, @PathVariable("serialId") String serialId) {
    Map<String, List<String>> serialIDSystems = new HashMap<>();
    logger.debug("Getting systems for serial id '{}'", serialId);
    try {
      serialIDSystems.putAll(databaseConnection.getSystemsForSerialID(serialId));
    } catch (Exception e) {
      logger.error("Error occurred in getSystemsForSerialId:", e);
      servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return serialIDSystems;
  }

  @PreDestroy
  public void shutdown() {
    databaseConnection.shutdown();
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
