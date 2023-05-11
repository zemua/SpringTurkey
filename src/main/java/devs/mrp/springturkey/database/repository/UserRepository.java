package devs.mrp.springturkey.database.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import devs.mrp.springturkey.database.entity.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, UUID> {

	public Mono<User> findByEmail(String email);

}
