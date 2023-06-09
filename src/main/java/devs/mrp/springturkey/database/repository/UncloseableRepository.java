package devs.mrp.springturkey.database.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import devs.mrp.springturkey.database.entity.Uncloseable;

public interface UncloseableRepository extends JpaRepository<Uncloseable, UUID> {

}
