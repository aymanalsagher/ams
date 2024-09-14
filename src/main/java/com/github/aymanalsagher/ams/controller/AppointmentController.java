package com.github.aymanalsagher.ams.controller;


import com.github.aymanalsagher.ams.controller.dto.AppointmentResponse;
import com.github.aymanalsagher.ams.controller.dto.NewAppointmentRequest;
import com.github.aymanalsagher.ams.service.AppointmentService;
import com.github.aymanalsagher.ams.service.CancelAppointmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1")
public class AppointmentController {

  private final AppointmentService appointmentService;

  @PostMapping("/appointments")
  @ResponseStatus(HttpStatus.CREATED)
  public void create(@RequestBody NewAppointmentRequest newAppointmentRequest) {
    appointmentService.create(newAppointmentRequest);
  }

  @PutMapping("/appointments")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancel(@RequestBody CancelAppointmentRequest request) {
    appointmentService.cancel(request);
  }

  @GetMapping("/appointments")
  public List<AppointmentResponse> get(@RequestParam(required = false) LocalDate date) {
    return appointmentService.get(date);
  }

}
