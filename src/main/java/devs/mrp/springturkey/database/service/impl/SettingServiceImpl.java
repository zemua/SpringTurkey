package devs.mrp.springturkey.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.springturkey.components.LoginDetailsReader;
import devs.mrp.springturkey.database.entity.Setting;
import devs.mrp.springturkey.database.entity.TurkeyUser;
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
	public Flux<Setting> findAllUserSettings(TurkeyUser user) {
		var list = settingRepository.findAllByUser(user);
		return Flux.fromIterable(list)
				.filter(setting -> loginDetailsReader.isCurrentUser(setting.getUser()));
	}

	@Override
	public Mono<Integer> saveSetting(Setting setting) {
		// TODO Auto-generated method stub
		return null;
	}

}
