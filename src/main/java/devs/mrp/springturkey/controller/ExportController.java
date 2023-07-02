package devs.mrp.springturkey.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/export")
public class ExportController {

	@GetMapping("/full")
	public Mono<ResponseEntity<Map<Object,Object>>> fullExport() {
		throw new RuntimeException("Not yet implemented");
	}

}
