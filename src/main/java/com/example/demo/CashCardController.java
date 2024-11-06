package com.example.demo;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	public ResponseEntity<CashCard> findById(Principal principal, @PathVariable Long id) {
		Optional<CashCard> cashcard = Optional.ofNullable(cashCardRepository.findByIdAndOwner(id, principal.getName()));
		System.out.println(cashcard + "*****************");
		if (cashcard.isPresent()) {
			return ResponseEntity.ok(cashcard.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public ResponseEntity<Void> createCashCard(Principal principal, @RequestBody CashCard cashCard,
			UriComponentsBuilder uriComponentsBuilder) {

		CashCard cashCardWithOwner = new CashCard(null, cashCard.amount(), principal.getName());
		CashCard saveCashCard = cashCardRepository.save(cashCardWithOwner);
		URI location = uriComponentsBuilder.path("/cashcard/{id}").buildAndExpand(saveCashCard.id()).toUri();
		return ResponseEntity.created(location).build();
	}

//	@GetMapping
//	public ResponseEntity<Iterable<CashCard>> findAll() {
//		return ResponseEntity.ok(cashCardRepository.findAll());
//	}

	@GetMapping
	public ResponseEntity<List<CashCard>> findAll(Principal principal, Pageable pageable) {
		Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
						pageable.getSortOr(Sort.by(Sort.Direction.DESC, "amount"))));
		return ResponseEntity.ok(page.getContent());
	}
}
