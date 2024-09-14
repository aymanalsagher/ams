package com.github.aymanalsagher.ams.service;

import com.github.aymanalsagher.ams.repository.CancellationReason;

public record CancelAppointmentRequest(Long appointmentId, CancellationReason reason) {}
