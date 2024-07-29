package org.ludus.ft7bot.repository;

import org.ludus.ft7bot.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {
    boolean existsByDiscordId(String discordId);
    boolean existsByUsername(String username);
    PlayerEntity findByDiscordId(String discordId);
    List<PlayerEntity> findAllByOrderByEloDesc();
}
