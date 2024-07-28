package org.ludus.ft7bot.repository;

import org.ludus.ft7bot.entity.DuelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DuelRepository extends JpaRepository<DuelEntity, Long> {
    @Query("""
            SELECT COUNT(d) > 0 FROM DuelEntity d WHERE
             (d.challenger.discordId = :pidOne AND d.opponent.discordId = :pidTwo
             OR
             d.challenger.discordId = :pidTwo AND d.opponent.discordId = :pidOne)
             AND
             d.status IN (ACCEPTED, PENDING)
            """)
    boolean existsByParticipantIds(String pidOne, String pidTwo);
    @Query("""
            SELECT d FROM DuelEntity d WHERE
             (d.challenger.discordId = :pidOne AND d.opponent.discordId = :pidTwo
             OR
             d.challenger.discordId = :pidTwo AND d.opponent.discordId = :pidOne)
             AND
             d.status = ACCEPTED
            """)
    DuelEntity findAcceptedDuelByParticipantIds(String pidOne, String pidTwo);
}
