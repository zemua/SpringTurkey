package devs.mrp.springturkey.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.DeltaEntity;

public interface DeltaRepository extends JpaRepository<DeltaEntity, Long> {

}
