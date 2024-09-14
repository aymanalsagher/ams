package com.github.aymanalsagher.ams.service.shared;

import java.time.LocalTime;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app")
public record ConfigProperties(LocalTime startTime, LocalTime endTime) {}
