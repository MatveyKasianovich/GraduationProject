package dev.sorokin.eventmanager.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity,Long> {
    void deleteLocationEntitiesById(Long id);
}
