package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.Group;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupService {

	public Flux<Group> findAllUserGroups();

	public Mono<Integer> addNewGroup(Group group);

}
