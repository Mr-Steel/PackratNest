package com.lucanet.packratcollector.consumers;

/**
 * Interface for the consumer that will receive and process HealthCheck messages.
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
public interface MessageConsumer {
  // ========================= Interface Variables =========================79
  // ============================ Public Methods ===========================79
  /**
   * Run the message consumer.
   */
  void run();
  /**
   * Stop the message consumer.
   */
  void stop();

  // =========================== Default Methods ===========================79
}
