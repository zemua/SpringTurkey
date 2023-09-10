package devs.mrp.springturkey.delta.validation;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.Delta;
import reactor.core.publisher.Mono;

public interface DataConstrainer {

	public Mono<Integer> pushDelta(Delta delta) throws WrongDataException;

}
