package org.ludus.ft7bot.repository;

import org.ludus.ft7bot.entity.DuelResultEntity;
import org.ludus.ft7bot.model.DuelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DuelResultRepository extends JpaRepository<DuelResultEntity, Long> {
    @Query("SELECT dr FROM DuelResultEntity dr WHERE dr.challenger.discordId = :challengerId AND dr.opponent.discordId = :opponentId AND dr.duelStatus = :status")
    DuelResultEntity findByChallengerAndOpponentDiscordIds(String challengerId, String opponentId, DuelStatus status);
}
