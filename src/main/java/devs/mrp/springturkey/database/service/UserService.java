package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.User;
import reactor.core.publisher.Mono;

public interface UserService {

	public Mono<User> addCurrentUser();

	public Mono<User> getUser();

}
