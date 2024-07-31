package org.ludus.ft7bot.service;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.ludus.ft7bot.constant.Buttons;
import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.entity.DuelEntity;
import org.ludus.ft7bot.entity.DuelResultEntity;
import org.ludus.ft7bot.entity.PlayerEntity;
import org.ludus.ft7bot.model.DuelStatus;
import org.ludus.ft7bot.repository.DuelRepository;
import org.ludus.ft7bot.repository.DuelResultRepository;
import org.ludus.ft7bot.repository.PlayerRepository;
import org.ludus.ft7bot.util.EloUtil;
import org.ludus.ft7bot.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DuelService {
    private static final Logger LOG = LoggerFactory.getLogger(DuelService.class);
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

    public String reportWinner(Event event, DuelEntity duelEntity, String reporterId, String opponentId, String winnerId) {
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
                saveConfirmedResult(duelEntity, reporterId, opponentId, winnerId);
                MessageUtil.sendByDiscordId(event, opponentId, Message.FT7_RESULT_CONFIRMED.formatted(playerRepository.findByDiscordId(opponentId).getElo()));
                return Message.FT7_RESULT_CONFIRMED.formatted(playerRepository.findByDiscordId(reporterId).getElo());
            } else {
                return Message.FAILED_TO_CONFIRM_RESULT.formatted(playerRepository.findByDiscordId(opponentId).getUsername());
            }
        } else {
            duelRepository.save(duelEntity);
            return Message.FT7_RESULT_REPORTED.formatted(playerRepository.findByDiscordId(opponentId).getUsername());
        }
    }

    public void updateChallengeStatus(ButtonInteractionEvent event, String duelId, DuelStatus status) {
        DuelEntity duel = duelRepository.findById(Long.parseLong(duelId)).orElseThrow();
        duel.setStatus(status);
        duelRepository.save(duel);

        String challengerDiscordId = duel.getChallenger().getDiscordId();
        String opponentUsername = duel.getOpponent().getUsername();
        String responseMessage = DuelStatus.ACCEPTED.equals(status)
                ? Message.FT7_ACCEPTED_BY_OPPONENT.formatted(opponentUsername)
                : Message.FT7_REJECTED_BY_OPPONENT.formatted(opponentUsername);
        MessageUtil.sendByDiscordId(event, challengerDiscordId, responseMessage, Button.primary(Buttons.REPORT_WIN_BUTTON + Buttons.SEPARATOR + duel.getNumId(), "Win"),
                Button.secondary(Buttons.CANCEL_DUEL + Buttons.SEPARATOR + duel.getNumId(), "Cancel"),
                Button.danger(Buttons.REPORT_LOSS_BUTTON + Buttons.SEPARATOR + duel.getNumId(), "Loss"));
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
