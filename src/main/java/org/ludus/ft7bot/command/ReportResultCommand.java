package org.ludus.ft7bot.command;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.ludus.ft7bot.constant.CommandName;
import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.entity.DuelResultEntity;
import org.ludus.ft7bot.entity.PlayerEntity;
import org.ludus.ft7bot.model.DuelStatus;
import org.ludus.ft7bot.repository.DuelResultRepository;
import org.ludus.ft7bot.repository.PlayerRepository;
import org.ludus.ft7bot.util.EloUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ReportResultCommand implements Command {
    private static final String CHALLENGER_OPTION = "challenger";
    private static final String OPPONENT_OPTION =  "opponent";
    private static final String WINNTER_OPTION = "winner";
    private final DuelResultRepository duelResultRepository;
    private final PlayerRepository playerRepository;

    public ReportResultCommand(DuelResultRepository duelResultRepository, PlayerRepository playerRepository) {
        this.duelResultRepository = duelResultRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return CommandName.REPORT_RESULT;
    }

    @Override
    public String getDescription() {
        return "Report ft7 score. Identical reports are required from both participants.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER,
                        CHALLENGER_OPTION,
                        "The challenger in the ft7",
                        true),
                new OptionData(OptionType.USER,
                        OPPONENT_OPTION,
                        "The opponent in the ft7",
                        true),
                new OptionData(OptionType.USER,
                        WINNTER_OPTION,
                        "The winner of the ft7",
                        true)
                );
    }

    @Override
    @Transactional
    public void execute(SlashCommandInteractionEvent event) {
        User challenger = event.getOption(CHALLENGER_OPTION).getAsUser();
        User opponent = event.getOption(OPPONENT_OPTION).getAsUser();
        DuelResultEntity duelResultEntity = duelResultRepository.findByChallengerAndOpponentDiscordIds(challenger.getId(), opponent.getId(), DuelStatus.ACCEPTED);
        if (duelResultEntity == null) {
            event.reply(Message.REQUESTED_DUEL_NOT_FOUND).setEphemeral(true).queue();
            return;
        }
        String winnerId = event.getOption(WINNTER_OPTION).getAsUser().getId();
        PlayerEntity winnerEntity = playerRepository.findByDiscordId(winnerId);
        PlayerEntity loserEntity = playerRepository.findByDiscordId(winnerId.equals(challenger.getId()) ? opponent.getId() : challenger.getId());

        double newWinnerRating = EloUtil.calculateNewElo(winnerEntity.getElo(), loserEntity.getElo(), EloUtil.WIN);
        double newLoserRating = EloUtil.calculateNewElo(loserEntity.getElo(), winnerEntity.getElo(), EloUtil.LOSS);

        duelResultEntity.setWinner(winnerEntity);
        duelResultEntity.setLoser(loserEntity);
        duelResultEntity.setDuelStatus(DuelStatus.FINISHED);
        duelResultEntity.setEloGain(newWinnerRating - winnerEntity.getElo());
        duelResultEntity.setEloLoss(loserEntity.getElo() - newLoserRating);
        duelResultRepository.save(duelResultEntity);

        winnerEntity.setElo(newWinnerRating);
        loserEntity.setElo(newLoserRating);
        playerRepository.save(winnerEntity);
        playerRepository.save(loserEntity);

        event.reply("Score confirmed successfully").setEphemeral(true).queue();
    }
}
