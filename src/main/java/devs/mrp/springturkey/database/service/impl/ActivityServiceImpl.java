package devs.mrp.springturkey.database.service.impl;

import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.User;
import devs.mrp.springturkey.database.service.ActivityService;
import reactor.core.publisher.Flux;

public class ActivityServiceImpl implements ActivityService {

	@Override
	public Flux<Activity> findAllUserActivites(User user) {
		// TODO Auto-generated method stub
		return null;
	}

}
