package com.github.aymanalsagher.ams.service;

import static java.time.ZoneOffset.UTC;

import com.github.aymanalsagher.ams.controller.dto.AppointmentResponse;
import com.github.aymanalsagher.ams.controller.dto.NewAppointmentRequest;
import com.github.aymanalsagher.ams.repository.*;
import com.github.aymanalsagher.ams.service.shared.ConfigProperties;
import com.github.aymanalsagher.ams.service.shared.DomainError;
import com.github.aymanalsagher.ams.service.shared.DomainException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AppointmentService {

  private static final String EMPTY_SPACE = " ";

  private final AppointmentRepository appointmentRepository;
  private final PatientRepository patientRepository;
  private final ConfigProperties configProperties;

  public void create(NewAppointmentRequest newAppointmentRequest) {

    Optional<Patient> patient = patientRepository.findById(newAppointmentRequest.patientId());

    if (patient.isEmpty()) {
      throw new DomainException("Patient not found", DomainError.RESOURCE_NOT_FOUND);
    }

    validateTime(newAppointmentRequest);

    Instant timeToSave =
        newAppointmentRequest.time().atZone(UTC).withMinute(0).withSecond(0).toInstant();

    if (appointmentRepository.exists(timeToSave)) {
      throw new DomainException("Slot is already taken", DomainError.VALIDATION);
    }

    Appointment appointment = new Appointment(null, timeToSave, null, patient.get());

    appointmentRepository.save(appointment);
  }

  private void validateTime(NewAppointmentRequest newAppointmentRequest) {
    Instant start =
        Instant.now()
            .atZone(UTC)
            .withHour(configProperties.startTime().get(ChronoField.CLOCK_HOUR_OF_DAY))
            .toInstant();
    Instant end =
        Instant.now()
            .atZone(UTC)
            .withHour(configProperties.endTime().get(ChronoField.CLOCK_HOUR_OF_DAY))
            .toInstant();
    Instant target =
        Instant.now()
            .atZone(UTC)
            .withHour(newAppointmentRequest.time().atZone(UTC).getHour())
            .toInstant();

    if (target.isBefore(start)
        || target.isAfter(end)
        || newAppointmentRequest.time().isBefore(Instant.now())) {
      throw new DomainException(
          "Slot is outside allowed range or in the past", DomainError.VALIDATION);
    }
  }

  public void cancel(CancelAppointmentRequest request) {
    Appointment appointment =
        appointmentRepository
            .findById(request.appointmentId())
            .orElseThrow(
                () -> new DomainException("Appointment not found", DomainError.RESOURCE_NOT_FOUND));

    appointment.setCancellationReason(request.reason());
    appointmentRepository.save(appointment);
  }

  public List<AppointmentResponse> get(LocalDate date) {

    LocalDate dateForSearch = date == null ? LocalDate.now() : date;

    Instant startOfDay = dateForSearch.atStartOfDay(UTC).toInstant();
    Instant endOfDay = dateForSearch.plusDays(1).atStartOfDay(UTC).toInstant().minusSeconds(1);

    return appointmentRepository.findAllBetween(startOfDay, endOfDay).stream()
        .map(
            appointment ->
                new AppointmentResponse(
                    appointment.getPatient().getFirstName()
                        + EMPTY_SPACE
                        + appointment.getPatient().getLastName(),
                    appointment.getSlotTime(),
                    appointment.getId(),
                    appointment.getCancellationReason()))
        .toList();
  }
}
