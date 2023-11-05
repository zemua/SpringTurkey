package devs.mrp.springturkey.controller.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import devs.mrp.springturkey.exceptions.AlreadyExistsException;
import devs.mrp.springturkey.exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.exceptions.DoesNotExistException;
import devs.mrp.springturkey.exceptions.TurkeySurpriseException;
import devs.mrp.springturkey.exceptions.WrongDataException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

	@ExceptionHandler(AlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public Mono<String> handleAlreadyExistsException(AlreadyExistsException ex) {
		log.error("Handling already exists exception: ", ex);
		return Mono.just("Already exists");
	}

	@ExceptionHandler(DoesNotBelongToUserException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public Mono<String> handleDoesNotBelongToUserException(DoesNotBelongToUserException ex) {
		log.error("Handling does not belong to user exception: ", ex);
		return Mono.just("Resource does not belong to user");
	}

	@ExceptionHandler(DoesNotExistException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public Mono<String> handleDoesNotExistException(DoesNotExistException ex) {
		log.error("Handling does not exist exception: ", ex);
		return Mono.just("Resource does not exist");
	}

	@ExceptionHandler(TurkeySurpriseException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Mono<String> handleTurkeySurpriseException(TurkeySurpriseException ex) {
		log.error("Handling surprise exception: ", ex);
		return Mono.just("Unexpected error happened");
	}

	@ExceptionHandler(WrongDataException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Mono<String> handleWrongDataException(WrongDataException ex) {
		log.error("Handling wrong data exception: ", ex);
		return Mono.just("Wrong data provided");
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Mono<String> handleGenericException(Exception ex) {
		log.error("Handling uncatch exception: ", ex);
		return Mono.just("Internal server error");
	}

}
