package devs.mrp.springturkey.database.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import devs.mrp.springturkey.database.entity.Setting;
import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface SettingRepository extends JpaRepository<Setting, UUID> {

	public List<Setting> findAllByUser(TurkeyUser user);

	@Transactional
	@Modifying
	@Query(value = "INSERT INTO turkey_setting (id, turkey_user, platform, setting_key, setting_value) VALUES (?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
	public int insert(UUID id, UUID user, String platform, String settingKey, String settingValue);

}
