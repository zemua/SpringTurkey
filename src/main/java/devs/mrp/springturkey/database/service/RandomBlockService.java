package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.RandomBlock;
import reactor.core.publisher.Flux;

public interface RandomBlockService {

	public Flux<RandomBlock> findAllUserBlocks();

}
