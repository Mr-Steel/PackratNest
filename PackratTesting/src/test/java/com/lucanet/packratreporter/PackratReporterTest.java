package com.lucanet.packratreporter;

import com.lucanet.util.MongoDaemon;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.ClassRule;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/reporter",
    glue = {
        "com.lucanet.packratcommon",
        "com.lucanet.packratreporter"
    },
    junit = {
        "--filename-compatible-names"
    },
    plugin = {
        "pretty",
        "json:build/reports/packrat_reporter.json",
        "junit:build/reports/packrat_reporter.xml"
    },
    tags = {
        "~@Ignore"
    }
)
public class PackratReporterTest {

  @ClassRule
  public static MongoDaemon mongoDaemon = new MongoDaemon();

}
