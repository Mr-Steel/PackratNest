package com.lucanet.packratcollector.deserializers;

import com.lucanet.packratcommon.model.HealthCheckHeader;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Implementation of {@link AbstractDeserializerTest} for testing the {@link HealthCheckHeaderDeserializer}
 */
class HealthCheckHeaderDeserializerTest extends AbstractDeserializerTest<HealthCheckHeader> {

  HealthCheckHeaderDeserializerTest() {
    super(new HealthCheckHeaderDeserializer());
  }

  @Test
  void invalidHeaderTest() throws Exception {
    List<HealthCheckHeader> invalidHeaderList = Arrays.asList(
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
    for (HealthCheckHeader invalidHeader : invalidHeaderList) {
      HealthCheckHeader deserializedHeader = deserializer.deserialize(
          "Test Topic",
          objectMapper.writeValueAsBytes(invalidHeader)
      );
      assertNull(deserializedHeader, () -> String.format("Object %s did not deserialize to null as expected!", deserializedHeader));
    }
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
        "Invalid One",
        2,
        true,
        new HashMap<>()
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
