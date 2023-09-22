package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.RandomBlock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RandomBlockService {

	public Flux<RandomBlock> findAllUserBlocks();

	public Mono<Integer> addNewBlock(RandomBlock block);

}
