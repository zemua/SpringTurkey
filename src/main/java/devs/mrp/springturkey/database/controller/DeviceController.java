package devs.mrp.springturkey.database.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.springturkey.database.controller.dto.DeviceIdDto;
import devs.mrp.springturkey.database.controller.dto.UserMailDto;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/device")
@Validated
public class DeviceController {

	@PostMapping("/add")
	public Mono<ResponseEntity<DeviceIdDto>> addDevice(@Valid @RequestBody Mono<UserMailDto> userEmal) {
		// TODO check that logged user is the same as the one in the requestBody
		return null;
	}

}
