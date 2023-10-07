package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.RandomCheck;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RandomCheckService {

	public Flux<RandomCheck> findAllUserChecks();

	public Mono<Integer> addNewCheck(RandomCheck check);

}
