package devs.mrp.springturkey.controller;

import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.springturkey.controller.dto.DeltaRequestDto;
import devs.mrp.springturkey.controller.dto.PushDeltaResponseDto;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "/deltas")
@Slf4j
public class DeltaController {

	// TODO enpoint to push deltas into the db
	@PostMapping(path = "/push", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Flux<PushDeltaResponseDto>> pushDeltas(@RequestBody List<DeltaRequestDto> deltas) {
		return new ResponseEntity<>(Flux.fromIterable(deltas.stream().map(d -> PushDeltaResponseDto.builder()
				.uuid(d.getRecordId())
				.success(true)
				.build()).toList()), HttpStatusCode.valueOf(201));
	}


	// TODO endpoint to export deltas from a given point onwards

}
