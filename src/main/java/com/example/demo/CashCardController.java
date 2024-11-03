package com.example.demo;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/cashcard")
public class CashCardController {

	@Autowired
	private CashCardRepository cashCardRepository;

	@GetMapping("/{id}")
	public ResponseEntity<CashCard> findById(@PathVariable Long id) {
		Optional<CashCard> cashcard = cashCardRepository.findById(id);

		if (cashcard.isPresent()) {
			return ResponseEntity.ok(cashcard.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard,
			UriComponentsBuilder uriComponentsBuilder) {

		CashCard saveCashCard = cashCardRepository.save(cashCard);
		URI location = uriComponentsBuilder.path("/cashcard/{id}").buildAndExpand(saveCashCard.id()).toUri();
		return ResponseEntity.created(location).build();
	}

	@GetMapping
	public ResponseEntity<Iterable<CashCard>> findAll() {
		return ResponseEntity.ok(cashCardRepository.findAll());
	}
}