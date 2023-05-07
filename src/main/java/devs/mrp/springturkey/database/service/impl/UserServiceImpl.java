package devs.mrp.springturkey.database.service.impl;

import org.springframework.stereotype.Service;

import devs.mrp.springturkey.database.entity.User;
import devs.mrp.springturkey.database.service.UserService;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

	@Override
	public Mono<User> addCurrentUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<User> getUser() {
		// TODO Auto-generated method stub
		return null;
	}

}
