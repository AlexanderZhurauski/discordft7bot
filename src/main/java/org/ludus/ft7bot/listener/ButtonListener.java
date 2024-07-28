package org.ludus.ft7bot.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.ludus.ft7bot.constant.Buttons;
import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.entity.DuelEntity;
import org.ludus.ft7bot.model.DuelStatus;
import org.ludus.ft7bot.repository.DuelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ButtonListener extends ListenerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(ButtonListener.class);
    private final DuelRepository duelRepository;

    public ButtonListener(DuelRepository duelRepository) {
        this.duelRepository = duelRepository;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] parts = event.getButton().getId().split(Buttons.SEPARATOR);
        String action = parts[0];
        if (action.equals(Buttons.ACCEPTED_BUTTON)) {
            updateChallengeStatus(event, parts[1], DuelStatus.ACCEPTED);
            event.reply(Message.FT7_ACCEPTED_BY_YOURSELF).setEphemeral(true).queue();
        } else if (action.equals(Buttons.REJECTED_BUTTON)) {
            updateChallengeStatus(event, parts[1], DuelStatus.CANCELLED);
            event.reply(Message.FT7_REJECTED_BY_YOURSELF).setEphemeral(true).queue();
        }
    }

    private void updateChallengeStatus(ButtonInteractionEvent event, String duelId, DuelStatus status) {
        DuelEntity duel = duelRepository.findById(Long.parseLong(duelId)).orElseThrow();
        duel.setStatus(status);
        duelRepository.save(duel);

        String challengerDiscordId = duel.getChallenger().getDiscordId();
        String challengerUsername = duel.getChallenger().getUsername();
        String responseMessage = DuelStatus.ACCEPTED.equals(status)
                ? Message.FT7_ACCEPTED_BY_OPPONENT.formatted(challengerUsername)
                : Message.FT7_REJECTED_BY_OPPONENT.formatted(challengerUsername);
        messageByDiscordId(event, challengerDiscordId, responseMessage);
    }

    private void messageByDiscordId(ButtonInteractionEvent event, String discordId, String message) {
        event.getJDA().retrieveUserById(discordId)
                .queue(user -> user.openPrivateChannel()
                        .flatMap(channel -> channel.sendMessage(message))
                        .queue(), throwable -> LOG.error(Message.USER_RETRIEVAL_FAILED, throwable));
    }

}
