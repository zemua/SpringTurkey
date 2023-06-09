package devs.mrp.springturkey.database.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.Condition;

public interface ConditionRepository extends JpaRepository<Condition, UUID> {

}
