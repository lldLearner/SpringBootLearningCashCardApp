package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
public class CashCardJsonTest {

	CashCard[] cashCards;

	@Autowired
	JacksonTester<CashCard[]> jsonList;

	@BeforeEach
	void setup() {
		cashCards = Arrays.array(new CashCard(99L, 123.45), new CashCard(100L, 100.00), new CashCard(101L, 150.00));
	}

	@Test
	void cashCardSerializationTest() throws IOException {
		
		assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
		
	}
	
	@Test
	void cashCardDeserializationTest() throws IOException {
		String expected="""
		         [
		            { "id": 99, "amount": 123.45 },
		            { "id": 100, "amount": 100.00 },
		            { "id": 101, "amount": 150.00 }
		         ]
		         """;
		assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
	}
}
