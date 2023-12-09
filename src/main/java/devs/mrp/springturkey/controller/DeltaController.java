package devs.mrp.springturkey.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.springturkey.controller.dto.DeltaRequestDto;
import devs.mrp.springturkey.controller.dto.PushDeltaResponseDto;
import devs.mrp.springturkey.database.service.DeltaServiceFacade;
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
	private final DeltaServiceFacade deltaServiceFacade;

	@PostMapping("/push")
	@PreAuthorize("isAuthenticated()")
	public Flux<PushDeltaResponseDto> pushDeltas(@RequestBody Flux<DeltaRequestDto> deltas) {
		return deltas
				.map(DeltaRequestDto::toDelta)
				.flatMap(this::insertedDeltaResponse);
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

	@GetMapping("/from/{position}")
	@PreAuthorize("isAuthenticated()")
	public Flux<Delta> getDeltasFromPosition(@PathVariable long position) {
		return deltaServiceFacade.findAfterPosition(position);
	}

}
