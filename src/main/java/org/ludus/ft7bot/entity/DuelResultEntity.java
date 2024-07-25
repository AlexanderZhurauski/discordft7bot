package org.ludus.ft7bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ludus.ft7bot.model.DuelStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "duel_result")
public class DuelResultEntity {
    @Id
    private Long numId;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private PlayerEntity winner;

    @ManyToOne
    @JoinColumn(name = "loser_id")
    private PlayerEntity loser;

    @Column
    private Double eloGain;

    @Column
    private Double eloLoss;

    @Enumerated(EnumType.ORDINAL)
    private DuelStatus duelStatus;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    private LocalDateTime lastModified;
}
