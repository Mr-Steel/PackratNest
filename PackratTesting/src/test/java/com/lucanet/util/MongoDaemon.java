package com.lucanet.util;

import org.junit.rules.ExternalResource;

import java.io.File;
import java.io.IOException;

/**
 * Class to ensure that the MongoDB Daemon is started for integration tests in case
 * the user has not started the MongoDB Daemon separately
 */
public class MongoDaemon extends ExternalResource {

  private Process mongoDaemon;

  @Override
  protected void before() throws Throwable {
    try {
      //Create output log file for mongod command. If this is not available, mongod's output buffer
      //will fill up and prevent any incoming connections
      File mongoDLogFile = File.createTempFile("mongodlog", ".tmp");
      System.out.println(String.format("Starting Mongo Daemon (if it isn't running already) with log at %s", mongoDLogFile.getAbsolutePath()));
      mongoDaemon = new ProcessBuilder("mongod", "--logpath", mongoDLogFile.getAbsolutePath()).start();
    } catch (IOException ioe) {
      System.err.println(String.format("Error starting Mongo Daemon: %s", ioe));
    }
  }

  @Override
  protected void after() {
    if ((mongoDaemon != null) && (mongoDaemon.isAlive())) {
      System.out.println("Stopping Mongo Daemon");
      mongoDaemon.destroy();
    }
  }
}
