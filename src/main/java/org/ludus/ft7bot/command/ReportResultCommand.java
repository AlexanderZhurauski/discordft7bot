package org.ludus.ft7bot.command;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.ludus.ft7bot.constant.CommandName;
import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.constant.OptionName;
import org.ludus.ft7bot.entity.DuelEntity;
import org.ludus.ft7bot.repository.DuelRepository;
import org.ludus.ft7bot.service.DuelService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportResultCommand implements Command {
    private final DuelService duelService;
    private final DuelRepository duelRepository;

    public ReportResultCommand(DuelService duelService, DuelRepository duelRepository) {
        this.duelService = duelService;
        this.duelRepository = duelRepository;
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
                        OptionName.OPPONENT_OPTION,
                        "Your opponent in the ft7",
                        true),
                new OptionData(OptionType.USER,
                        OptionName.WINNER_OPTION,
                        "The winner of the ft7",
                        true)
                );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User reporter = event.getUser();
        User opponent = event.getOption(OptionName.OPPONENT_OPTION).getAsUser();

        DuelEntity duelEntity = duelRepository.findAcceptedDuelByParticipantIds(reporter.getId(), opponent.getId());
        if (duelEntity == null) {
            event.reply(Message.REQUESTED_DUEL_NOT_FOUND).setEphemeral(true).queue();
            return;
        }

        String winnerId = event.getOption(OptionName.WINNER_OPTION).getAsUser().getId();
        if (!duelEntity.getChallenger().getDiscordId().equals(winnerId)
                && !duelEntity.getOpponent().getDiscordId().equals(winnerId)) {
            event.reply(Message.INVALID_WINNER_SPECIFIED).setEphemeral(true).queue();
        }

        event.reply(duelService.reportWinner(duelEntity, reporter.getId(), opponent.getId(), winnerId)).setEphemeral(true).queue();
    }
}
