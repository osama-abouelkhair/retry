package com.baeldung.retry;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableRetry
public class RetryApplication implements CommandLineRunner {

	private BaeldungService baeldungService;

	public RetryApplication(BaeldungService baeldungService) {
		this.baeldungService = baeldungService;
	}

	public static void main(String[] args) {
		SpringApplication.run(RetryApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		baeldungService.get();
	}
}

@RestController
class BaeldungController {

	@GetMapping("/get")
	public ResponseEntity get() {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
	}

}

@Service
class BaeldungService {

	private RestTemplate restTemplate = new RestTemplate();

	@Retryable(maxAttempts=5, value = HttpServerErrorException.class, backoff = @Backoff(delay = 5000))
	public void get() {
		restTemplate.getForObject("http://localhost:8081/get", String.class);
	}

	/**
	 * To log the max attempts achieved
	 * @param e
	 */
	@Recover
	void recover(Exception e) {
		System.out.println(e);
	}

}
