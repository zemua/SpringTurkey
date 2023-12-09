package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.delta.Delta;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeltaServiceFacade {

	public Mono<Integer> pushCreation(Delta delta);

	public Mono<Integer> pushModification(Delta delta);

	public Mono<Integer> pushDeletion(Delta delta);

	public Flux<Delta> findAfterPosition(Long position);

}
