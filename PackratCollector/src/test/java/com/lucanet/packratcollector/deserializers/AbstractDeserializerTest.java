package com.lucanet.packratcollector.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractDeserializerTest<T> {

  private final ObjectMapper objectMapper;
  private final Deserializer<T> deserializer;

  AbstractDeserializerTest(Deserializer<T> deserializer) {
    this.objectMapper = new ObjectMapper();
    this.deserializer = deserializer;
  }

  @Test
  @DisplayName("Deserializing an ideal instance")
  void normalDeserializationTest() throws Exception {
    T normalInstance = getNormalInstance();
    T deserializedInstance = deserializer.deserialize(
        "Test Topic",
        objectMapper.writeValueAsBytes(normalInstance)
    );
    assertAll("Normal Instance",
        () -> assertNotNull(deserializedInstance),
        () -> assertEquals(normalInstance, deserializedInstance)
    );
  }

  @ParameterizedTest
  @MethodSource("getInvalidTypes")
  @DisplayName("Deserializing invalid data types")
  void invalidTypesTest(Object invalidType) throws Exception {
    T deserializedInstance = deserializer.deserialize(
        "Test Topic",
        objectMapper.writeValueAsBytes(invalidType)
    );
    assertNull(deserializedInstance, () -> String.format("Object %s did not deserialize to null as expected!", invalidType));
  }

  @Test
  @DisplayName("Deserializing an empty instance of the data type")
  void noDataTest() throws Exception {
    T emptyInstance = getEmptyInstance();
    T deserializedInstance = deserializer.deserialize(
        "Test Topic",
        objectMapper.writeValueAsBytes(emptyInstance)
    );
    assertNull(deserializedInstance);
  }

  @ParameterizedTest
  @MethodSource("getNullInput")
  @DisplayName("Deserializing null input")
  void nullInputTest(byte[] nullInput) throws Exception {
    T deserializedInstance = deserializer.deserialize("Test Topic", nullInput);
    assertNull(deserializedInstance);
  }

  protected abstract T getNormalInstance();
  protected abstract Stream<Object> getInvalidTypes();
  protected abstract T getEmptyInstance();

  private Stream<byte[]> getNullInput() {
    return Stream.of(
        new byte[0],
        null
    );
  }
}
