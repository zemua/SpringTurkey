package devs.mrp.springturkey.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.springturkey.controller.dto.DeltaRequestDto;
import devs.mrp.springturkey.controller.dto.PushDeltaResponseDto;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.validation.DataPushConstrainerProvider;
import devs.mrp.springturkey.exceptions.WrongDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/deltas")
@Slf4j
@RequiredArgsConstructor
public class DeltaController {

	private final DataPushConstrainerProvider dataPushConstrainer;

	// TODO enpoint to push deltas into the db
	@PostMapping(path = "/push", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Flux<PushDeltaResponseDto>> pushDeltas(@RequestBody Flux<DeltaRequestDto> deltas) {
		Flux<PushDeltaResponseDto> responseFlux = deltas
				.map(DeltaRequestDto::toDelta)
				.flatMap(this::insertedDeltaResponse);

		return new ResponseEntity<Flux<PushDeltaResponseDto>>(responseFlux, HttpStatusCode.valueOf(201));
	}

	private Mono<PushDeltaResponseDto> insertedDeltaResponse(Delta delta) {
		return pushData(delta)
				.map(insertedQty -> PushDeltaResponseDto.builder()
						.uuid(delta.getRecordId())
						.success(insertedQty > 0)
						.build());
	}

	private Mono<Integer> pushData(Delta delta) {
		try {
			return dataPushConstrainer.getFor(delta.getDeltaType())
					.pushDelta(delta);
		} catch (WrongDataException e) {
			log.warn("Delta contains wrong data {}", delta, e);
			return Mono.just(0);
		}
	}


	// TODO endpoint to export deltas from a given point onwards

}
