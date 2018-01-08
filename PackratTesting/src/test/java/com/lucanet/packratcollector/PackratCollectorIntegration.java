package com.lucanet.packratcollector;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
    classes = PackratCollector.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration
@TestPropertySource(locations = "classpath:packrat_collector.properties")
@AutoConfigureMockMvc
abstract class PackratCollectorIntegration {
}
