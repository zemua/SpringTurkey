package devs.mrp.springturkey.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.Setting;
import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface SettingRepository extends JpaRepository<Setting, UUID> {

	public List<Setting> findAllByUser(TurkeyUser user);

}
