package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.repository.GroupRepository;
import devs.mrp.springturkey.database.service.GroupService;
import devs.mrp.springturkey.exceptions.AlreadyExistsException;
import devs.mrp.springturkey.exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.utils.Duple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GroupServiceImpl implements GroupService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private GroupRepository groupRepository;

	@Override
	public Flux<Group> findAllUserGroups() {
		return loginDetailsReader.getTurkeyUser().flatMapMany(user -> Flux.fromIterable(groupRepository.findAllByUser(user)))
				.flatMap(group -> loginDetailsReader.isCurrentUser(group.getUser())
						.map(isCurrent -> new Duple<Group,Boolean>(group, isCurrent)))
				.filter(Duple::getValue2)
				.map(Duple::getValue1);
	}

	@Override
	public Mono<Integer> addNewGroup(Group group) {
		return loginDetailsReader.isCurrentUser(group.getUser())
				.flatMap(isCurrent -> {
					if (!Boolean.TRUE.equals(isCurrent)) {
						return Mono.error(new DoesNotBelongToUserException());
					}
					try {
						return insert(group);
					} catch (DataIntegrityViolationException e) {
						return Mono.error(new AlreadyExistsException());
					}
				});
	}

	private Mono<Integer> insert(Group group) {
		if (group.getId() == null) {
			return Mono.just(groupRepository.save(group))
					.map(grp -> grp != null ? 1 : 0);
		} else {
			return Mono.just(groupRepository.insert(
					group.getId(),
					group.getUser().getId(),
					group.getName(),
					group.getType().name()
					));
		}
	}

}
