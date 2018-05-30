package com.mr.reservationsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class ReservationSvcApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReservationSvcApplication.class, args);
  }
}
