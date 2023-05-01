package devs.mrp.springturkey.database.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import devs.mrp.springturkey.database.entity.User;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

}
