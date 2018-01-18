package com.lucanet.packratcollector.deserializers;

import java.util.*;
import java.util.stream.Stream;

/**
 * Implementation of {@link AbstractDeserializerTest} for testing the {@link JSONDeserializer}
 */
class JSONDeserializerTest extends AbstractDeserializerTest<Map<String, Object>> {

  JSONDeserializerTest() {
    super(new JSONDeserializer());
  }

  @Override
  protected Map<String, Object> getNormalInstance() {
    Map<String, Object> sampleData = new HashMap<>();
    sampleData.put("TEST ONE", 1);
    sampleData.put("TEST TWO", 2);
    sampleData.put("TEST THREE", 3);
    return sampleData;
  }

  @Override
  protected Stream<Object> getInvalidTypes() {
    return Stream.of(
        "Invalid One",
        2,
        true,
        new ArrayList<>()
    );
  }

  @Override
  protected Map<String, Object> getEmptyInstance() {
    return Collections.emptyMap();
  }
}
