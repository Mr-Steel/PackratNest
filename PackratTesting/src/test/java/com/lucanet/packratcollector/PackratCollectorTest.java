package com.lucanet.packratcollector;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/collector",
    glue = {
        "com.lucanet.packratcommon",
        "com.lucanet.packratcollector"
    }
)
public class PackratCollectorTest {
}
