package devs.mrp.springturkey.database.service;

import java.util.UUID;

import devs.mrp.springturkey.database.entity.dto.ExportData;
import reactor.core.publisher.Mono;

public interface FullUserDumpFacade {

	public Mono<ExportData> fullUserDump(UUID currentDeviceId);

}
