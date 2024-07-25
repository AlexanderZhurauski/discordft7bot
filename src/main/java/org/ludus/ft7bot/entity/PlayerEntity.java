package org.ludus.ft7bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@NoArgsConstructor
@Entity
@Table(name = "player")
public class PlayerEntity {
    @Id
    private Long numId;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String discordId;

    @Column
    private Double elo;

    @OneToMany(mappedBy = "winner")
    private Collection<DuelResultEntity> wonDuels;

    @OneToMany(mappedBy = "loser")
    private Collection<DuelResultEntity> lostDuels;

    @CreatedDate
    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registered;
}
