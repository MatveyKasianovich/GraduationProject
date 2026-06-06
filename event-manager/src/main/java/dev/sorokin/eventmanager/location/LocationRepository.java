package dev.sorokin.eventmanager.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity,Long> {
    void deleteLocationEntitiesById(Long id);
    Optional<LocationEntity> findById(Long id);
}
