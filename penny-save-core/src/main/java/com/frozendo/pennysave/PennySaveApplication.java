package com.frozendo.pennysave;

import com.frozendo.pennysave.service.TestSecurityService;
import com.frozendo.pennysave.service.TestShareService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PennySaveApplication {

	private final TestShareService testShareService;
	private final TestSecurityService testSecurityService;

    public PennySaveApplication(TestShareService testShareService, TestSecurityService testSecurityService) {
        this.testShareService = testShareService;
        this.testSecurityService = testSecurityService;
    }

    public static void main(String[] args) {
		SpringApplication.run(PennySaveApplication.class, args);
	}

	@GetMapping("/security")
	public String testSecurity() {
		return testSecurityService.test();
	}

	@GetMapping("/share")
	public String testShare() {
		return testShareService.test();
	}

}
