package devs.mrp.springturkey.delta.validation;

import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.exceptions.WrongDataException;
import reactor.core.publisher.Mono;

public interface DataConstrainer {

	public Mono<Integer> pushDelta(Delta delta) throws WrongDataException;

}
