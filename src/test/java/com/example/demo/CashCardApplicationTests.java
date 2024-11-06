package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class CashCardApplicationTests {

	@Autowired
	TestRestTemplate testRestTemplate;

	@Test
	void shouldReturnACashCardWhenDataIsSaved() {
		ResponseEntity<String> response = testRestTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/cashcard/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id");
		assertThat(id).isEqualTo(99);
	}

	@Test
	void shouldReturnEmptyForInvalidCashCard() {
		ResponseEntity<String> response = testRestTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/cashcard/10000", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test
	@DirtiesContext
	void shouldReturnAllCashCardsWhenListIsRequested() {
		ResponseEntity<String> response = testRestTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/cashcard", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number length = documentContext.read("$.length()");
		assertThat(length).isEqualTo(3);

		JSONArray ids = documentContext.read("$..id");
		assertThat(ids).containsExactly(101, 99, 100);

		JSONArray amounts = documentContext.read("$..amount");
		assertThat(amounts).containsExactly(150.00, 123.45, 100.00);
//		JSONArray page = documentContext.read("$[*]");
//		assertThat(page.size()).isEqualTo(1);
		
//		double amount = documentContext.read("$[0].amount");
//		assertThat(amount).isEqualTo(150.00);
	}

	@Test
	void shouldCreateANewCashCard() {
		CashCard cashCard = new CashCard(null, 143.25, null);

		ResponseEntity<Void> response = testRestTemplate.withBasicAuth("sarah1", "abc123").postForEntity("/cashcard", cashCard, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI location = response.getHeaders().getLocation();
		ResponseEntity<String> getResponse = testRestTemplate.withBasicAuth("sarah1", "abc123").getForEntity(location, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Number amount = documentContext.read("$.amount");
		assertThat(id).isNotNull();
		assertThat(amount).isEqualTo(143.25);
	}
	
	@Test
	void shoudlRejectNonAuthorizesUser() {
		ResponseEntity<String> cashCard = testRestTemplate.withBasicAuth("hank-has-no-role", "sqs123").getForEntity("/cashcard/99", String.class);
		assertThat(cashCard.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}
	
	@Test
	void shouldNotAcessUnowneCashCard() {
		ResponseEntity<String> cashCard = testRestTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/cashcard/102", String.class);
		assertThat(cashCard.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
