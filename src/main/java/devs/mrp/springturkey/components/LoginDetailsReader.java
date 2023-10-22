package devs.mrp.springturkey.components;

import devs.mrp.springturkey.database.entity.TurkeyUser;
import reactor.core.publisher.Mono;

public interface LoginDetailsReader {

	public Mono<String> getUserId();

	public boolean isCurrentUser(TurkeyUser user);

	public Mono<TurkeyUser> getTurkeyUser();

	public Mono<TurkeyUser> setupCurrentUser();

}
