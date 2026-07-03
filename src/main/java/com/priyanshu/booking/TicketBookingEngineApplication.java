package com.priyanshu.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = "com.priyanshu.booking")
@EnableJpaRepositories(basePackages = "com.priyanshu.booking.repository")
@EnableKafka
@EnableCaching
public class TicketBookingEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketBookingEngineApplication.class, args);
    }
}