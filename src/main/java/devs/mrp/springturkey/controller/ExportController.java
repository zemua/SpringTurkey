package devs.mrp.springturkey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.springturkey.controller.dto.ExportDataDto;
import devs.mrp.springturkey.database.service.FullUserDumpFacade;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/export")
public class ExportController {

	@Autowired
	private FullUserDumpFacade dumpFacade;

	@GetMapping("/full")
	public Mono<ResponseEntity<ExportDataDto>> fullExport() {
		return Mono.just(new ResponseEntity<ExportDataDto>(new ExportDataDto(), HttpStatusCode.valueOf(200)));
	}

}
