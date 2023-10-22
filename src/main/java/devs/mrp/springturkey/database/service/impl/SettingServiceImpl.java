package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.Exceptions.AlreadyExistsException;
import devs.mrp.springturkey.Exceptions.DoesNotBelongToUserException;
import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.Setting;
import devs.mrp.springturkey.database.repository.SettingRepository;
import devs.mrp.springturkey.database.service.SettingService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SettingServiceImpl implements SettingService {

	@Autowired
	private LoginDetailsReader loginDetailsReader;

	@Autowired
	private SettingRepository settingRepository;

	@Override
	public Flux<Setting> findAllUserSettings() {
		return loginDetailsReader.getTurkeyUser()
				.map(user -> settingRepository.findAllByUser(user))
				.flatMapMany(list -> Flux.fromIterable(list))
				.filter(setting -> loginDetailsReader.isCurrentUser(setting.getUser()));
	}

	@Override
	public Mono<Integer> addNewSetting(Setting setting) {
		if (!loginDetailsReader.isCurrentUser(setting.getUser())) {
			log.error("Activity does not belong to user");
			log.debug(setting.toString());
			return Mono.error(new DoesNotBelongToUserException());
		}
		try {
			return insert(setting);
		} catch(DataIntegrityViolationException e) {
			log.error("Error inserting condition", e);
			return Mono.error(new AlreadyExistsException());
		}
	}

	private Mono<Integer> insert(Setting setting) {
		if (setting.getId() == null) {
			return Mono.just(settingRepository.save(setting))
					.map(s -> s != null ? 1 : 0);
		} else {
			return Mono.just(settingRepository.insert(
					setting.getId(),
					setting.getUser().getId(),
					setting.getPlatform().name(),
					setting.getSettingKey(),
					setting.getSettingValue()
					));
		}
	}

}
