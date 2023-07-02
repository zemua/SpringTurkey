package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.Condition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConditionService {

	public Flux<Condition> findAllUserConditions();

	public Mono<Integer> addNewCondition(Condition condition);

}
