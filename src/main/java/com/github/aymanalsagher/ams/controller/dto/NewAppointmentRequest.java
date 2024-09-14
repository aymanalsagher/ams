package com.github.aymanalsagher.ams.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record NewAppointmentRequest(@NotNull Long patientId, @NotNull Instant time) {}
