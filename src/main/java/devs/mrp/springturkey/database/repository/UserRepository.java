package devs.mrp.springturkey.database.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

	public User findByEmail(String email);

}
