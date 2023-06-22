package devs.mrp.springturkey.database.service;

import devs.mrp.springturkey.database.entity.Setting;
import devs.mrp.springturkey.database.entity.TurkeyUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SettingService {

	public Flux<Setting> findAllUserSettings(TurkeyUser user);

	public Mono<Integer> addNewSetting(Setting setting);

}
