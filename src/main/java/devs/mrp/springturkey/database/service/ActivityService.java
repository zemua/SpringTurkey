package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.User;
import reactor.core.publisher.Flux;

public interface ActivityService {

	public Flux<Activity> findAllUserActivites(User user);

}
