package devs.mrp.springturkey.database.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface UserRepository extends JpaRepository<TurkeyUser, UUID> {

	// TODO try changing driver to a reactive one

	public Optional<TurkeyUser> findByExternalId(String externalId);

	// TODO create method to force insert and throw error if trying to update
	// @Query(value = "INSERT INTO my_table (field1, field2) VALUES (:#{#c.field1}, :#{#c.field2})", nativeQuery = true)
	// public void insert(@Param("c") MyRecord c);

}
