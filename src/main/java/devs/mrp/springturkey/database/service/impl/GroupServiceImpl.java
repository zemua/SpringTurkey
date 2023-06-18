package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.repository.GroupRepository;
import devs.mrp.springturkey.database.service.GroupService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GroupServiceImpl implements GroupService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private GroupRepository groupRepository;

	@Override
	public Flux<Group> findAllUserGroups(TurkeyUser user) {
		return Flux.fromIterable(groupRepository.findAllByUser(user))
				.filter(activity -> loginDetailsReader.isCurrentUser(activity.getUser()));
	}

	@Override
	public Mono<Integer> addNewGroup(Group group) {
		if (!loginDetailsReader.isCurrentUser(group.getUser())) {
			return Mono.just(0);
		}
		return Mono.just(groupRepository.insert(
				group.getId(),
				group.getUser().getId(),
				group.getName(),
				group.getType().name()
				));
	}

}
