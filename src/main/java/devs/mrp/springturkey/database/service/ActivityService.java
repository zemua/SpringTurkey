package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.Activity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ActivityService {

	public Flux<Activity> findAllUserActivites();

	public Mono<Integer> addNewActivity(Activity activity);

}
