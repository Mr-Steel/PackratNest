package com.lucanet.packratreporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan({"com.lucanet.packratreporter", "com.lucanet.packratcommon"})
public class PackratReporter {

  public static void main(String[] args) {
    SpringApplication.run(PackratReporter.class, args);
  }

}
