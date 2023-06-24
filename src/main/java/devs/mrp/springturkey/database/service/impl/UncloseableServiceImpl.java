package devs.mrp.springturkey.database.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.Activity;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.Uncloseable;
import devs.mrp.springturkey.database.repository.ActivityRepository;
import devs.mrp.springturkey.database.service.UncloseableService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UncloseableServiceImpl implements UncloseableService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private ActivityRepository activityRepository;

	@Override
	public Flux<Uncloseable> findAllUserUncloseables(TurkeyUser user) {
		return Flux.fromIterable(activityRepository.findAllByUser(user))
				.filter(activity -> loginDetailsReader.isCurrentUser(activity.getUser()))
				.filter(activity -> Objects.nonNull(activity.getUncloseable()))
				.map(Activity::getUncloseable);
	}

	@Override
	public Mono<Integer> addNewUncloseable(Uncloseable uncloseable) {
		// TODO Auto-generated method stub
		return null;
	}

}
