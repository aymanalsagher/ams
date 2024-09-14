package com.github.aymanalsagher.ams.repository;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PATIENT")
public class Patient {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "PATIENT_SEQ_GENERATOR")
  @TableGenerator(
      name = "PATIENT_SEQ_GENERATOR",
      table = "APP_SEQ_GENERATOR",
      pkColumnName = "SEQ_NAME",
      pkColumnValue = "PATIENT_SEQ_PK",
      valueColumnName = "SEQ_VALUE",
      initialValue = 1,
      allocationSize = 1)
  private Long id;

  private String firstName;
  private String lastName;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "patient")
  private List<Appointment> appointments = new ArrayList<>();

  public void add(Appointment appointment) {
    this.appointments.add(appointment);
    appointment.setPatient(this);
  }

  public List<Appointment> getAppointments() {
    return Collections.unmodifiableList(appointments);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Patient current = (Patient) o;

    return getId() != null && getId().equals(current.getId());
  }

  @Override
  public int hashCode() {
    if (getId() == null) return getClass().hashCode();
    else return Objects.hash(this.getId());
  }
}
