package devs.mrp.springturkey.database.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import devs.mrp.springturkey.database.entity.User;

public interface UserRepository extends CrudRepository<User, UUID> {

	public User findByEmail(String email);

}
