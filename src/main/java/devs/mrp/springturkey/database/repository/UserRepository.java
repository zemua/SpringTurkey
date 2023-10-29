package devs.mrp.springturkey.database.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.TurkeyUser;

public interface UserRepository extends JpaRepository<TurkeyUser, UUID> {

	public Optional<TurkeyUser> findByEmail(String email);

}
