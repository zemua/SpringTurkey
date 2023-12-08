package devs.mrp.springturkey.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.springturkey.controller.dto.ExportDataDto;
import devs.mrp.springturkey.database.service.FullUserDumpFacade;
import devs.mrp.springturkey.exceptions.WrongDataException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/export")
@Slf4j
public class ExportController {

	@Autowired
	private FullUserDumpFacade dumpFacade;

	@GetMapping("/full/{deviceid}")
	@PreAuthorize("isAuthenticated()")
	public Mono<ResponseEntity<ExportDataDto>> fullExport(@PathVariable String deviceid) {
		UUID id;
		try {
			id = UUID.fromString(deviceid);
		} catch(IllegalArgumentException e) {
			log.error("Invalid UUID requested for export: {}", deviceid, e);
			return Mono.error(new WrongDataException("Invalid device id"));
		}
		return dumpFacade.fullUserDump(id)
				.map(ExportDataDto::from)
				.map(data -> new ResponseEntity<>(data, HttpStatusCode.valueOf(200)));
	}

}
