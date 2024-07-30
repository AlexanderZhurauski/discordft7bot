package org.ludus.ft7bot.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.ludus.ft7bot.constant.Buttons;
import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.entity.DuelEntity;
import org.ludus.ft7bot.model.DuelStatus;
import org.ludus.ft7bot.repository.DuelRepository;
import org.ludus.ft7bot.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CancelDuelButton implements ButtonAction {
    private static final Logger LOG = LoggerFactory.getLogger(CancelDuelButton.class);
    private final DuelRepository duelRepository;
    private final PlayerRepository playerRepository;

    public CancelDuelButton(DuelRepository duelRepository, PlayerRepository playerRepository) {
        this.duelRepository = duelRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        String reporterId = event.getUser().getId();
        String duelId = event.getButton().getId().split(Buttons.SEPARATOR)[1];
        DuelEntity duelEntity = duelRepository.findById(Long.parseLong(duelId)).orElseThrow();
        if (DuelStatus.CANCELLED.equals(duelEntity.getStatus()) || DuelStatus.FINISHED.equals(duelEntity.getStatus())) {
            event.reply(Message.DUEL_FINISHED_OR_CANCELLED).setEphemeral(true).queue();
            event.getMessage().editMessageComponents().queue();
            return;
        }
        duelEntity.setStatus(DuelStatus.CANCELLED);
        duelRepository.save(duelEntity);
        event.getMessage().editMessageComponents().queue();
        String opponentId = reporterId.equals(duelEntity.getChallenger().getDiscordId()) ? duelEntity.getOpponent().getDiscordId() : reporterId;
        messageByDiscordId(event, opponentId, Message.FT7_CANCELLED_SUCCESSFULLY.formatted(playerRepository.findByDiscordId(reporterId).getUsername()));
        event.reply(Message.FT7_CANCELLED_SUCCESSFULLY.formatted(playerRepository.findByDiscordId(opponentId).getUsername())).setEphemeral(true).queue();
    }

    @Override
    public String getAction() {
        return Buttons.CANCEL_DUEL;
    }

    private void messageByDiscordId(ButtonInteractionEvent event, String discordId, String message) {
        event.getJDA().retrieveUserById(discordId)
                .queue(user -> user.openPrivateChannel()
                        .flatMap(channel -> channel.sendMessage(message))
                        .queue(), throwable -> LOG.error(Message.USER_RETRIEVAL_FAILED, throwable));
    }
}
