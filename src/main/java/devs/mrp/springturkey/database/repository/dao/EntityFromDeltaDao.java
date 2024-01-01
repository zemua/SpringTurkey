package devs.mrp.springturkey.database.repository.dao;

import devs.mrp.springturkey.delta.Delta;
import reactor.core.publisher.Mono;

public interface EntityFromDeltaDao {

	public Mono<Integer> persistTurkeyDataFromDelta(Delta delta);

}
