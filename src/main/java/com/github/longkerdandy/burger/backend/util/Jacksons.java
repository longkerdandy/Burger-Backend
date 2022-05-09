package com.github.longkerdandy.burger.backend.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.experimental.UtilityClass;

/**
 * Jackson (JSON) util.
 */
@UtilityClass
public final class Jacksons {

  /**
   * Get the singleton of pre-configured {@link ObjectMapper}.
   *
   * @return {@link ObjectMapper}
   */
  public static ObjectMapper getMapper() {
    return SingletonHelper.MAPPER;
  }

  /**
   * Get the singleton of pre-configured {@link ObjectWriter}.
   *
   * @return {@link ObjectWriter}
   */
  public ObjectWriter getWriter() {
    return SingletonHelper.MAPPER.writer();
  }

  /**
   * Get the singleton of pre-configured {@link ObjectReader}.
   *
   * @param type of instance
   * @return {@link ObjectReader}
   */
  public ObjectReader getReader(JavaType type) {
    return SingletonHelper.MAPPER.readerFor(type);
  }

  /**
   * Get the singleton of pre-configured {@link ObjectReader}.
   *
   * @param type of instance
   * @return {@link ObjectReader}
   */
  public ObjectReader getReader(Class<?> type) {
    return SingletonHelper.MAPPER.readerFor(type);
  }

  /**
   * Get the singleton of pre-configured {@link ObjectReader}.
   *
   * @param type of instance
   * @return {@link ObjectReader}
   */
  public ObjectReader getReader(TypeReference<?> type) {
    return SingletonHelper.MAPPER.readerFor(type);
  }

  /**
   * Prior to Java 5, java memory model had a lot of issues and other approaches used to fail in
   * certain scenarios where too many threads try to get the instance of the Singleton class
   * simultaneously. So Bill Pugh came up with a different approach to create the Singleton class
   * using a inner static helper class.
   *
   * <p>Notice the private inner static class that contains the instance of the singleton class.
   * When the singleton class is loaded, SingletonHelper class is not loaded into memory and only
   * when someone calls the getInstance method, this class gets loaded and creates the Singleton
   * class instance.
   */
  private static class SingletonHelper {

    private static final ObjectMapper MAPPER;

    static {
      // Jackson 3.0 changes things as it requires Java 8 to work and can thereby directly
      // supported features. Because of this parameter-names and datatypes modules are merged
      // into jackson-databind and need not be registered; datetime module (JavaTimeModule)
      // remains separate module due to its size and configurability options. So you will only
      // need to separately add "Java 8 Date/time" module (see above for description)
      MAPPER = JsonMapper.builder()
          .addModule(new ParameterNamesModule())
          .addModule(new Jdk8Module())
          .addModule(new JavaTimeModule())
          .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
          .build();

      // Serialization
      MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
      MAPPER.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
      MAPPER.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
      MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

      // Deserialization
      MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
      MAPPER.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false);
      MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      MAPPER.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
      MAPPER.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    }
  }
}
