package nnt_data.yanki_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Yanki_Service {

	public static void main(String[] args) {
		SpringApplication.run(Yanki_Service.class, args);
	}

}
