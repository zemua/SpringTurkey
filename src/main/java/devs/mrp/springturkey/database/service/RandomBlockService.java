package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.RandomQuestion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RandomBlockService {

	public Flux<RandomQuestion> findAllUserBlocks();

	public Mono<Integer> addNewBlock(RandomQuestion block);

}
