package devs.mrp.springturkey.database.service;

import java.util.Map;
import java.util.UUID;

import reactor.core.publisher.Mono;

public interface FullUserDumpFacade {

	public Mono<Map<Object,Object>> fullUserDump(UUID currentDeviceId);

}
