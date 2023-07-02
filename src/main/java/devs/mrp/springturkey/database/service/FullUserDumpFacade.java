package devs.mrp.springturkey.database.service;

import java.util.Map;

import devs.mrp.springturkey.database.entity.Device;
import reactor.core.publisher.Mono;

public interface FullUserDumpFacade {

	public Mono<Map<Object,Object>> fullUserDump(Device currentDevice);

}
