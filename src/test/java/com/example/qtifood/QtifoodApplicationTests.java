package com.example.qtifood;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QtifoodApplicationTests {

	static {
		// Provide dummy Gemini config for context startup in tests
		System.setProperty("GEMINI_API_KEY", "test-key");
		System.setProperty("GEMINI_MODEL", "gemini-3-pro-preview");
	}

	@Test
	void contextLoads() {
	}

}
