package com.example.support_ticket_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SupportTicketSystemApplication {

	public static void main(String[] args) {
		System.out.println("TICKET APPLICATION STARTED");
		SpringApplication.run(SupportTicketSystemApplication.class, args);
	}

}
