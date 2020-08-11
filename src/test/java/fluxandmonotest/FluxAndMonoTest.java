package fluxandmonotest;

import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

	@Test
	public void fluxTest() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Tools")
				.concatWith(Flux.error(new RuntimeException()))
				.concatWith(Flux.just("After Error")) // this will not be sent as error was added in flux which will block this afterwards
				.log();
		
		stringFlux.subscribe(System.out::println, 
				e -> System.err.print(e), // this will be called whenever any exception occurs
				() -> System.out.print("Everything is completed")); // this is called when subscriber is finished with data and there is nothing left to sent, this will only be called when everything was successful with no errors
	}
	
	@Test
	public void fluxTestWithoutErrors() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Tools");
		StepVerifier.create(stringFlux)
			.expectNext("Spring")
			.expectNext("Spring Boot", "Reactor Tools")
			.verifyComplete();
	}
	
	@Test
	public void fluxTestWithErrors() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Tools")
				.concatWith(Flux.error(new RuntimeException()));
		
		StepVerifier.create(stringFlux)
			.expectNext("Spring")
			.expectNext("Spring Boot", "Reactor Tools")
			.expectError()
			.verify();
	}
	
	@Test
	public void monoTestWithoutErrors() {
		Mono<String> monoFlux = Mono.just("Spring").log();
		
		StepVerifier.create(monoFlux)
			.expectNext("Spring")
			.verifyComplete();
	}
	
	@Test
	public void monoTestWithErrors() {
		StepVerifier.create(Mono.error(new RuntimeException("Exception")).log())
			.expectError(RuntimeException.class)
			.verify();
				
	}
	
}
