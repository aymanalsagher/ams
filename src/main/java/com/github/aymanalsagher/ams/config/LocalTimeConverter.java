package com.github.aymanalsagher.ams.config;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class LocalTimeConverter implements Converter<String, LocalTime> {
  @Override
  public LocalTime convert(@NonNull String source) {
    return LocalTime.parse(source, DateTimeFormatter.ofPattern("HH:mm"));
  }
}
