package devs.mrp.springturkey.database.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.repository.UserRepository;
import devs.mrp.springturkey.database.service.UserService;
import devs.mrp.springturkey.exceptions.DoesNotExistException;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Mono<TurkeyUser> createCurrentUser() {
		return loginDetailsReader.getUserId().map(user -> TurkeyUser.builder()
				.externalId(user)
				.build())
				.flatMap(user -> Mono.just(userRepository.save(user)));
	}

	@Override
	public Mono<TurkeyUser> getUser() {
		return loginDetailsReader.getUserId()
				.flatMap(user -> Mono.just(userRepository.findByExternalId(user)))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.switchIfEmpty(Mono.error(new DoesNotExistException("User does not exist")));
	}

}
