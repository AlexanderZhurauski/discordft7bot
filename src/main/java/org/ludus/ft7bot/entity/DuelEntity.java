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
@Table(name = "duel")
public class DuelEntity {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numId;

    @ManyToOne
    @JoinColumn(name = "challenger")
    private PlayerEntity challenger;

    @ManyToOne
    @JoinColumn(name = "opponent")
    private PlayerEntity opponent;

    @ManyToOne
    @JoinColumn(name = "winner_ch")
    private PlayerEntity reportedWinnerByCh;

    @ManyToOne
    @JoinColumn(name = "winner_op")
    private PlayerEntity reportedWinnerByOp;

    @Enumerated(EnumType.ORDINAL)
    private DuelStatus status = DuelStatus.PENDING;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime lastModified;
}
