package com.mr.reservationsvc.data;

import com.mr.reservationsvc.model.Reservation;
import com.mr.reservationsvc.repository.ReservationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SampleData implements CommandLineRunner {

  private final ReservationRepository reservationRepository;

  public SampleData(ReservationRepository reservationRepository) {
    this.reservationRepository = reservationRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    Stream.of("Matias", "Camila", "Laura", "Adrian", "Josefina")
        .forEach(name -> reservationRepository.save(new Reservation(name)));
    reservationRepository.findAll().forEach(System.out::println);
  }
}
