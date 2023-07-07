package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.Setting;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SettingService {

	public Flux<Setting> findAllUserSettings();

	public Mono<Integer> addNewSetting(Setting setting);

}
