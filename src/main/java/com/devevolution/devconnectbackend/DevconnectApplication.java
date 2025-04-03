package com.devevolution.devconnectbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.devevolution.devconnectbackend")
public class DevconnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevconnectApplication.class, args);
	}

}
