package com.mr.reservationclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableZuulProxy
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReservationClientApplication.class, args);
  }
}

@EnableCircuitBreaker
@RestController
@RequestMapping("/reservations")
class ReservationApiAdapterRestController {

  private final ReservationReader reservationReader;

  @Autowired
  ReservationApiAdapterRestController(ReservationReader reservationReader) {
    this.reservationReader = reservationReader;
  }

  public Collection<String> fallback() {
    return Stream.of("This", "is", "data", "from", "fallback")
            .collect(Collectors.toList());
  }

  @HystrixCommand(fallbackMethod = "fallback")
  @GetMapping("/names")
  public Collection<String> names() {
    return reservationReader
        .read()
        .getContent()
        .stream()
        .map(Reservation::getReservationName)
        .collect(Collectors.toList());
  }
}

@FeignClient("reservation-service")
interface ReservationReader {

  @RequestMapping(method = RequestMethod.GET, value = "/reservations")
  Resources<Reservation> read();
}

class Reservation {

  private String reservationName;

  public String getReservationName() {
    return reservationName;
  }
}
