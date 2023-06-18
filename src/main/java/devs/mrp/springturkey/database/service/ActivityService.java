package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ActivityService {

	public Flux<Activity> findAllUserActivites(TurkeyUser user);

	public Mono<Integer> addNewActivity(Activity activity);

}
