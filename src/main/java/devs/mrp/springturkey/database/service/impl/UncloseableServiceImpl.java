package devs.mrp.springturkey.database.service.impl;

import devs.mrp.springturkey.database.entity.TurkeyUser;
import devs.mrp.springturkey.database.entity.Uncloseable;
import devs.mrp.springturkey.database.service.UncloseableService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class UncloseableServiceImpl implements UncloseableService {

	@Override
	public Flux<Uncloseable> findAllUserUncloseables(TurkeyUser user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Integer> addNewUncloseable(Uncloseable uncloseable) {
		// TODO Auto-generated method stub
		return null;
	}

}
