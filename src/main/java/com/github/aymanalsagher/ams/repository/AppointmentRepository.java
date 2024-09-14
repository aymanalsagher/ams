package com.github.aymanalsagher.ams.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  @Query(
      "SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END "
          + "FROM Appointment a "
          + "WHERE a.slotTime = :slotTime")
  boolean exists(@Param("slotTime") Instant slotTime);

  // Custom query to find all appointments for today
  @Query("SELECT a FROM Appointment a WHERE a.slotTime >= :startOfDay AND a.slotTime < :endOfDay")
  List<Appointment> findAllBetween(
      @Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay);
}
