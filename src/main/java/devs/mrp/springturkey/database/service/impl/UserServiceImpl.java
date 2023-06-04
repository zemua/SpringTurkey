package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.User;
import devs.mrp.springturkey.database.repository.UserRepository;
import devs.mrp.springturkey.database.service.UserService;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Mono<User> addCurrentUser() {
		User user = User.builder()
				.email(loginDetailsReader.getUsername())
				.build();
		return Mono.just(userRepository.save(user));
	}

	@Override
	public Mono<User> getUser() {
		return Mono.just(userRepository.findByEmail(loginDetailsReader.getUsername()));
	}

}
