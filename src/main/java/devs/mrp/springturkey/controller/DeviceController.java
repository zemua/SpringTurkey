package devs.mrp.springturkey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.springturkey.controller.dto.DeviceIdDto;
import devs.mrp.springturkey.database.service.UserDeviceFacade;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/device")
@Validated
@Slf4j
public class DeviceController {

	@Autowired
	private UserDeviceFacade userDeviceFacade;

	@PostMapping("/add")
	public Mono<ResponseEntity<DeviceIdDto>> addDevice() {
		return userDeviceFacade.addDevice()
				.map(device -> DeviceIdDto.builder().id(device.getId()).build())
				.doOnNext(idDto -> log.debug("Added device id: {}", idDto.getId()))
				.map(dto -> new ResponseEntity<DeviceIdDto>(dto, HttpStatusCode.valueOf(201)));
	}

}
