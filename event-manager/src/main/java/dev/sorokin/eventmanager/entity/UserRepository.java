package dev.sorokin.eventmanager.entity;


import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    boolean existsUserEntityByLogin(String login);

    Optional<UserEntity> findByLogin(String login);

    boolean existsUserEntityById(Long id);

    Optional<UserEntity> findById(Long id);
}
