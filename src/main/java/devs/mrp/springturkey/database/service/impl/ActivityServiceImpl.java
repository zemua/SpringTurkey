package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.repository.ActivityRepository;
import devs.mrp.springturkey.database.service.ActivityService;
import devs.mrp.springturkey.exceptions.AlreadyExistsException;
import devs.mrp.springturkey.exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.utils.Duple;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ActivityServiceImpl implements ActivityService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private ActivityRepository activityRepository;

	@Override
	public Flux<Activity> findAllUserActivites() {
		return loginDetailsReader.getTurkeyUser().flatMapMany(user -> Flux.fromIterable(activityRepository.findAllByUser(user)))
				.flatMap(activity -> loginDetailsReader.isCurrentUser(activity.getUser())
						.map(isCurrent -> new Duple<Activity,Boolean>(activity, isCurrent)))
				.filter(Duple::getValue2)
				.map(Duple::getValue1);
	}

	@Override
	public Mono<Integer> addNewActivity(Activity activity) {
		return loginDetailsReader.isCurrentUser(activity.getUser())
				.flatMap(isCurrentUser -> {
					if (!Boolean.TRUE.equals(isCurrentUser)) {
						log.error("Activity does not belong to user");
						log.debug(activity.toString());
						return Mono.error(new DoesNotBelongToUserException());
					}
					try {
						return insert(activity);
					} catch (DataIntegrityViolationException e) {
						log.error("Error inserting condition", e);
						return Mono.error(new AlreadyExistsException());
					}
				});

	}

	private Mono<Integer> insert(Activity activity) {
		if (activity.getId() == null) {
			return Mono.just(activityRepository.save(activity))
					.map(act -> act != null ? 1 : 0);
		} else {
			return Mono.just(activityRepository.insert(
					activity.getId(),
					activity.getUser().getId(),
					activity.getActivityName(),
					activity.getActivityType().name(),
					activity.getCategoryType().name()
					));
		}
	}

}
