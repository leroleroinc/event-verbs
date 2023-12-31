package com.lerolero.verbs.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;

import reactor.core.publisher.Flux;
import java.util.function.Function;

import com.lerolero.verbs.services.VerbService;

@RestController
@RequestMapping("/verbs")
public class VerbController {

	@Autowired
	private VerbService verbService;

	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> get(@RequestParam(defaultValue = "1") Integer size) {
		return verbService.randomVerbList(size);
	}

	@GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> subscribe(@RequestParam(defaultValue = "200") Integer interval) {
		return verbService.randomVerbProducer(interval).onBackpressureDrop();
	}

	@Bean
	public Function<Flux<String>,Flux<String>> verbfunction() {
		return flux -> flux
			.doOnNext(size -> System.out.println("VERBS: Processing " + size))
			.flatMap(size -> verbService.randomVerbList(Integer.parseInt(size)));
	}

}
