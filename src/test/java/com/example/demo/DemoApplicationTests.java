package com.example.demo;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoApplicationTests {
	@Autowired
	private SimpleService simpleService;

	@Transactional
	@Test
	void incorrectBulkUpdateTest() {
		simpleService.add("John", "google");
		simpleService.add("Nick", "facebook");
		simpleService.add("Anna", "netflix");

		var found = simpleService.findByName("John");

		assertEquals(1, found.size());
		assertEquals("google", found.get(0).company);

		simpleService.incorrectBulkUpdate(
				Map.of(
						"amazon", List.of("Nick", "Anna"),
						"tesla", List.of("John")
				)
		);

		found = simpleService.findByName("John");

		assertEquals(1, found.size());
		assertEquals("tesla", found.get(0).company);
	}

	@Transactional
	@Test
	void correctBulkUpdateTest() {
		simpleService.add("John", "google");
		simpleService.add("Nick", "facebook");
		simpleService.add("Anna", "netflix");

		var found = simpleService.findByName("John");

		assertEquals(1, found.size());
		assertEquals("google", found.get(0).company);

		simpleService.correctBulkUpdate(
				Map.of(
						"amazon", List.of("Nick", "Anna"),
						"tesla", List.of("John")
				)
		);

		found = simpleService.findByName("John");

		assertEquals(1, found.size());
		assertEquals("tesla", found.get(0).company);
	}
}
