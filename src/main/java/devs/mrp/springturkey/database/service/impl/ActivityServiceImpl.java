package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.repository.ActivityRepository;
import devs.mrp.springturkey.database.service.ActivityService;
import reactor.core.publisher.Flux;

public class ActivityServiceImpl implements ActivityService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private ActivityRepository activityRepository;

	@Override
	public Flux<Activity> findAllUserActivites(TurkeyUser user) {
		return Flux.fromIterable(activityRepository.findAllByUser(user))
				.filter(activity -> loginDetailsReader.isCurrentUser(activity.getUser()));
	}

}
