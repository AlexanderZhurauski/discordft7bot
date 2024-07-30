package org.ludus.ft7bot.command;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.ludus.ft7bot.constant.CommandName;
import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.constant.OptionName;
import org.ludus.ft7bot.entity.DuelEntity;
import org.ludus.ft7bot.model.DuelStatus;
import org.ludus.ft7bot.repository.DuelRepository;
import org.ludus.ft7bot.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CancelCommand implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(CancelCommand.class);
    private final DuelRepository duelRepository;
    private final PlayerRepository playerRepository;

    public CancelCommand(DuelRepository duelRepository, PlayerRepository playerRepository) {
        this.duelRepository = duelRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return CommandName.CANCEL_DUEL;
    }

    @Override
    public String getDescription() {
        return "Cancel the ft7 with the given opponent";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER,
                OptionName.OPPONENT_OPTION,
                "Your opponent in the ft7",
                true));
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

        duelEntity.setStatus(DuelStatus.CANCELLED);
        duelRepository.save(duelEntity);
        String opponentId = reporter.getId().equals(duelEntity.getChallenger().getDiscordId()) ? duelEntity.getOpponent().getDiscordId() : reporter.getId();
        messageByDiscordId(event, opponentId, Message.FT7_CANCELLED_SUCCESSFULLY.formatted(playerRepository.findByDiscordId(reporter.getId()).getUsername()));
        event.reply(Message.FT7_CANCELLED_SUCCESSFULLY.formatted(playerRepository.findByDiscordId(opponentId).getUsername())).setEphemeral(true).queue();
    }

    private void messageByDiscordId(SlashCommandInteractionEvent event, String discordId, String message) {
        event.getJDA().retrieveUserById(discordId)
                .queue(user -> user.openPrivateChannel()
                        .flatMap(channel -> channel.sendMessage(message))
                        .queue(), throwable -> LOG.error(Message.USER_RETRIEVAL_FAILED, throwable));
    }
}
