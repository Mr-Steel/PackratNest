package com.lucanet.packratreporter;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/reporter",
    glue = {
        "com.lucanet.packratcommon",
        "com.lucanet.packratreporter"
    },
    tags = {
        "~@Ignore"
    }
)
public class PackratReporterTest {
}
