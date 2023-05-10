package devs.mrp.springturkey.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import devs.mrp.springturkey.Exceptions.DoesNotBelongToUserException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class TurkeyExceptionHandler {

	@ExceptionHandler(DoesNotBelongToUserException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public Mono<String> handleDoesNotBelongToUserException(DoesNotBelongToUserException ex) {
		log.error("Handling exception: ", ex);
		return Mono.just("Resource does not belong to this user");
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Mono<String> handleGenericException(Exception ex) {
		log.error("Handling uncatch exception: ", ex);
		return Mono.just("Internal server error");
	}

}
