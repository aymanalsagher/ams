package com.github.aymanalsagher.ams.repository;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "APPOINTMENT")
public class Appointment {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "APPOINTMENT_SEQ_GENERATOR")
  @TableGenerator(
      name = "APPOINTMENT_SEQ_GENERATOR",
      table = "APP_SEQ_GENERATOR",
      pkColumnName = "SEQ_NAME",
      pkColumnValue = "APPOINTMENT_SEQ_PK",
      valueColumnName = "SEQ_VALUE",
      initialValue = 1,
      allocationSize = 1)
  private Long id;

  @Column(name = "SLOT_TIME", nullable = false)
  private Instant slotTime;

  @Enumerated(EnumType.STRING)
  private CancellationReason cancellationReason;

  @ManyToOne
  @JoinColumn(name = "PATIENT_ID", nullable = false)
  private Patient patient;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Appointment current = (Appointment) o;

    return getId() != null && getId().equals(current.getId());
  }

  @Override
  public int hashCode() {
    if (getId() == null) return getClass().hashCode();
    else return Objects.hash(this.getId());
  }
}
