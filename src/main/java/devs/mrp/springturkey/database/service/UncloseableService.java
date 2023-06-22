package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.Uncloseable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UncloseableService {

	public Flux<Uncloseable> findAllUserUncloseables(TurkeyUser user);

	public Mono<Integer> addNewUncloseable(Uncloseable uncloseable);

}
