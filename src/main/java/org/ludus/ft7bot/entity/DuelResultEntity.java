package org.ludus.ft7bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ludus.ft7bot.model.DuelStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "duel_result")
public class DuelResultEntity {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Setter(AccessLevel.NONE)
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime lastModified;
}
