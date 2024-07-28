package org.ludus.ft7bot.service;

import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.entity.DuelEntity;
import org.ludus.ft7bot.entity.DuelResultEntity;
import org.ludus.ft7bot.entity.PlayerEntity;
import org.ludus.ft7bot.model.DuelStatus;
import org.ludus.ft7bot.repository.DuelRepository;
import org.ludus.ft7bot.repository.DuelResultRepository;
import org.ludus.ft7bot.repository.PlayerRepository;
import org.ludus.ft7bot.util.EloUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DuelService {
    private final DuelRepository duelRepository;
    private final DuelResultRepository duelResultRepository;
    private final PlayerRepository playerRepository;

    public DuelService(DuelRepository duelRepository,
                       DuelResultRepository duelResultRepository,
                       PlayerRepository playerRepository) {
        this.duelRepository = duelRepository;
        this.duelResultRepository = duelResultRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public String reportWinner(DuelEntity duelEntity, String reporterId, String opponentId, String winnerId) {
        String challengerId = duelEntity.getChallenger().getDiscordId();

        if (reporterId.equals(challengerId)) {
            duelEntity.setReportedWinnerByCh(playerRepository.findByDiscordId(winnerId));
        } else {
            duelEntity.setReportedWinnerByOp(playerRepository.findByDiscordId(winnerId));
        }

        PlayerEntity winnerByCh = duelEntity.getReportedWinnerByCh();
        PlayerEntity winnerByOp = duelEntity.getReportedWinnerByOp();
        if (winnerByCh != null && winnerByOp != null) {
            if (winnerByCh.getDiscordId().equals(winnerByOp.getDiscordId())) {
                reportWinner(duelEntity, reporterId, opponentId, winnerId);
                return Message.FT7_RESULT_CONFIRMED.formatted(playerRepository.findByDiscordId(reporterId).getElo());
            } else {
                return Message.FAILED_TO_CONFIRM_RESULT.formatted(opponentId);
            }
        } else {
            duelRepository.save(duelEntity);
            return Message.FT7_RESULT_REPORTED.formatted(playerRepository.findByDiscordId(opponentId).getUsername());
        }
    }

    private void saveConfirmedResult(DuelEntity duelEntity, String reporterId, String opponentId, String winnerId) {
        PlayerEntity winnerEntity = playerRepository.findByDiscordId(winnerId);
        PlayerEntity loserEntity = playerRepository.findByDiscordId(winnerId.equals(reporterId) ? opponentId : reporterId);

        double newWinnerRating = EloUtil.calculateNewElo(winnerEntity.getElo(), loserEntity.getElo(), EloUtil.WIN);
        double newLoserRating = EloUtil.calculateNewElo(loserEntity.getElo(), winnerEntity.getElo(), EloUtil.LOSS);

        DuelResultEntity duelResultEntity = new DuelResultEntity();
        duelResultEntity.setWinner(winnerEntity);
        duelResultEntity.setLoser(loserEntity);
        duelResultEntity.setEloGain(newWinnerRating - winnerEntity.getElo());
        duelResultEntity.setEloLoss(loserEntity.getElo() - newLoserRating);
        duelResultRepository.save(duelResultEntity);

        duelEntity.setStatus(DuelStatus.FINISHED);
        duelRepository.save(duelEntity);

        winnerEntity.setElo(newWinnerRating);
        loserEntity.setElo(newLoserRating);
        playerRepository.save(winnerEntity);
        playerRepository.save(loserEntity);
    }
}
