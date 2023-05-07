package devs.mrp.springturkey.database.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import devs.mrp.springturkey.Exceptions.DoesNotBelongToUserException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class TurkeyExceptionHandlerTest {

	private TurkeyExceptionHandler exceptionHandler;

	@BeforeEach
	void setup() {
		exceptionHandler = new TurkeyExceptionHandler();
	}

	@Test
	void testHandleDoesNotBelongToUserException() {
		DoesNotBelongToUserException ex = new DoesNotBelongToUserException("does not belong to this user");
		Mono<String> response = exceptionHandler.handleDoesNotBelongToUserException(ex);
		StepVerifier.create(response)
		.expectNext("Resource does not belong to this user")
		.verifyComplete();
	}

	@Test
	void testGenericExceptionHandler() {
		Exception ex = new Exception("random error");
		Mono<String> response = exceptionHandler.handleGenericException(ex);
		StepVerifier.create(response)
		.expectNext("Internal server error")
		.verifyComplete();
	}

}
