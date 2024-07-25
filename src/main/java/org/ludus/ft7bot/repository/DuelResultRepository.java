package org.ludus.ft7bot.repository;

import org.ludus.ft7bot.entity.DuelResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DuelResultRepository extends JpaRepository<DuelResultEntity, Long> {
}
