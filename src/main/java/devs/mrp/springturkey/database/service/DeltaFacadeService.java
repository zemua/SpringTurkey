package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.delta.Delta;
import reactor.core.publisher.Mono;

public interface DeltaFacadeService {

	public Mono<Integer> pushCreation(Delta delta);

	public Mono<Integer> pushModification(Delta delta);

	public Mono<Integer> pushDeletion(Delta delta);

}
