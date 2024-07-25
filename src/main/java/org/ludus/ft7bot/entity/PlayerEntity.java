package org.ludus.ft7bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "player")
public class PlayerEntity {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numId;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String discordId;

    @Column
    private Double elo = 1000.0;

    @OneToMany(mappedBy = "winner")
    private Collection<DuelResultEntity> wonDuels;

    @OneToMany(mappedBy = "loser")
    private Collection<DuelResultEntity> lostDuels;

    @CreatedDate
    @Column(name = "registration_date", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime registered;
}
