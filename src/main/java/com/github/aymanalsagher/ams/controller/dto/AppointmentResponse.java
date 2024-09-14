package com.github.aymanalsagher.ams.controller.dto;

import com.github.aymanalsagher.ams.repository.CancellationReason;

import java.time.Instant;

public record AppointmentResponse(String patientName, Instant time, Long appointmentId,
                                  CancellationReason cancellationReason) {}
