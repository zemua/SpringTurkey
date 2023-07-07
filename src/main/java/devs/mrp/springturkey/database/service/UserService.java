package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.TurkeyUser;
import reactor.core.publisher.Mono;

public interface UserService {

	public Mono<TurkeyUser> addCurrentUser();

	public Mono<TurkeyUser> getUser();

}
