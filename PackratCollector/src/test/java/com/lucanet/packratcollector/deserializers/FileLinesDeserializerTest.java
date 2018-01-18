package com.lucanet.packratcollector.deserializers;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * Implementation of {@link AbstractDeserializerTest} for testing the {@link FileLinesDeserializer}
 */
class FileLinesDeserializerTest extends AbstractDeserializerTest<List<String>> {

  FileLinesDeserializerTest() {
    super(new FileLinesDeserializer());
  }

  @Override
  protected List<String> getNormalInstance() {
    return Arrays.asList("LINE ONE", "LINE TWO", "LINE THREE");
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
  protected List<String> getEmptyInstance() {
    return Collections.emptyList();
  }
}
