package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.delta.Delta;
import reactor.core.publisher.Mono;

public interface DeltaFacadeService {

	public Mono<Integer> pushCreation(Delta delta);

	public int pushModification(Delta delta);

	public int pushDeletion(Delta delta);

}
