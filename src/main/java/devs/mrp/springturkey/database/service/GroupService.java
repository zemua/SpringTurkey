package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.Group;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupService {

	public Flux<Group> findAllUserGroups(TurkeyUser user);

	public Mono<Integer> addNewGroup(Group group);

}
