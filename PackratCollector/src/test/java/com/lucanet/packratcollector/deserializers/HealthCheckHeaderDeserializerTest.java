package com.lucanet.packratcollector.deserializers;

import com.lucanet.packratcommon.model.HealthCheckHeader;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Implementation of {@link AbstractDeserializerTest} for testing the {@link HealthCheckHeaderDeserializer}
 */
@DisplayName("Validate the HealthCheckHeader Deserializer")
class HealthCheckHeaderDeserializerTest extends AbstractDeserializerTest<HealthCheckHeader> {

  HealthCheckHeaderDeserializerTest() {
    super(new HealthCheckHeaderDeserializer());
  }

  @Override
  protected HealthCheckHeader getNormalInstance() {
    return generateHealthCheckHeader(
        "Serial-AAAA",
        "System-AAAA1111",
        1111111111L,
        1111111111L,
        1
    );
  }

  @Override
  protected Stream<Object> getInvalidTypes() {
    return Stream.of(
        //Test invalid object types
        "Invalid One",
        2,
        true,
        new HashMap<>(),
        //Test HealthCheckHeaders that should fail validation
        generateHealthCheckHeader(
            null,
            "System-AAAA1111",
            1111111111L,
            1111111111L,
            1
        ),
        generateHealthCheckHeader(
            "Serial-AAAA",
            null,
            1111111111L,
            1111111111L,
            1
        ),
        generateHealthCheckHeader(
            "Serial-AAAA",
            "System-AAAA1111",
            null,
            1111111111L,
            1
        ),
        generateHealthCheckHeader(
            "Serial-AAAA",
            "System-AAAA1111",
            1111111111L,
            null,
            1
        ),
        generateHealthCheckHeader(
            "Serial-AAAA",
            "System-AAAA1111",
            1111111111L,
            1111111111L,
            null
        ),
        generateHealthCheckHeader(
            "Serial-AAAA",
            "System-AAAA1111",
            1111111111L,
            1111111111L,
            -1
        )
    );
  }

  @Override
  protected HealthCheckHeader getEmptyInstance() {
    return new HealthCheckHeader();
  }

  private HealthCheckHeader generateHealthCheckHeader(
      String serialId,
      String systemUUID,
      Long sessionTimestamp,
      Long healthCheckTimestamp,
      Integer version
  ) {
    HealthCheckHeader healthCheckHeader = new HealthCheckHeader();
    healthCheckHeader.setSerialId(serialId);
    healthCheckHeader.setSystemUUID(systemUUID);
    healthCheckHeader.setSessionTimestamp(sessionTimestamp);
    healthCheckHeader.setHealthCheckTimestamp(healthCheckTimestamp);
    healthCheckHeader.setVersion(version);

    return healthCheckHeader;
  }
}
