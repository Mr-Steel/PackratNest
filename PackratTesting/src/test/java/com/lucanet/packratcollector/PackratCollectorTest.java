package com.lucanet.packratcollector;

import com.lucanet.util.MongoDaemon;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.kafka.test.rule.KafkaEmbedded;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/collector",
    glue = {
        "com.lucanet.packratcommon",
        "com.lucanet.packratcollector"
    },
    junit = {
        "--filename-compatible-names"
    },
    plugin = {
        "pretty",
        "json:build/reports/packrat_collector.json",
        "junit:build/reports/packrat_collector.xml"
    },
    tags = {
        "~@Ignore"
    }
)
public class PackratCollectorTest {

  @ClassRule
  public static MongoDaemon mongoDaemon = new MongoDaemon();

  @ClassRule
  public static KafkaEmbedded kafkaEmbedded = new KafkaEmbedded(
      1,
      true,
      1,
      "DynamicSystemStats", "StaticSystemStats", "SummaDatabase", "TransactionStats"
  );
}
