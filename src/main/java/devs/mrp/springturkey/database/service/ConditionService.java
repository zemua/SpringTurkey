package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.Condition;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConditionService {

	public Flux<Condition> findAllUserConditions(TurkeyUser user);

	public Mono<Integer> addNewCondition(Condition condition);

}
