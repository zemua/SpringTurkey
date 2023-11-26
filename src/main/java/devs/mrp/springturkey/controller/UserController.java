package devs.mrp.springturkey.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.springturkey.database.service.UserService;
import devs.mrp.springturkey.exceptions.DoesNotExistException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/user")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/exists")
	public Mono<ResponseEntity<Boolean>> currentUserExists() {
		return userService.getUser()
				.map(user -> new ResponseEntity<>(Objects.nonNull(user), HttpStatusCode.valueOf(200)))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(false, HttpStatusCode.valueOf(200))))
				.onErrorReturn(DoesNotExistException.class, new ResponseEntity<>(false, HttpStatusCode.valueOf(200)));
	}

	// TODO create user
	@PostMapping("/create")
	public Mono<ResponseEntity<String>> createCurrentUser() {
		return null;
	}

}
