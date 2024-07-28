package org.ludus.ft7bot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.ludus.ft7bot.constant.Buttons;
import org.ludus.ft7bot.constant.CommandName;
import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.entity.DuelEntity;
import org.ludus.ft7bot.entity.PlayerEntity;
import org.ludus.ft7bot.repository.DuelRepository;
import org.ludus.ft7bot.repository.PlayerRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class Ft7Command implements Command {
    private static final String PLAYER_OPTION = "player";
    private final DuelRepository duelRepository;
    private final PlayerRepository playerRepository;

    public Ft7Command(DuelRepository duelRepository, PlayerRepository playerRepository) {
        this.duelRepository = duelRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return CommandName.FT7;
    }

    @Override
    public String getDescription() {
        return "Challenge the specified player to a ft7 duel";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER,
                PLAYER_OPTION,
                "The player to challenge",
                true));
    }

    @Override
    @Transactional
    public void execute(SlashCommandInteractionEvent event) {
        final String challengerId = event.getUser().getId();
        if (!playerRepository.existsByDiscordId(challengerId)) {
            event.reply(Message.CHALLENGER_NOT_REGISTERED).setEphemeral(true).queue();
            return;
        }

        final String opponentId = event.getOption(PLAYER_OPTION).getAsUser().getId();
        if (!playerRepository.existsByDiscordId(opponentId)) {
            event.reply(Message.OPPONENT_NOT_REGISTERED).setEphemeral(true).queue();
            return;
        }

        if (challengerId.equals(opponentId)) {
            event.reply(Message.SELF_CHALLENGE_IMPOSSIBLE).setEphemeral(true).queue();
            return;
        }

        if (duelRepository.existsByParticipantIds(challengerId, opponentId)) {
            event.reply(Message.DUEL_ALREADY_IN_PROGRESS).setEphemeral(true).queue();
            return;
        }

        final PlayerEntity challenger = playerRepository.findByDiscordId(challengerId);
        final PlayerEntity opponent = playerRepository.findByDiscordId(opponentId);

        final DuelEntity duelEntity = new DuelEntity();
        duelEntity.setChallenger(challenger);
        duelEntity.setOpponent(opponent);
        final DuelEntity savedDuel = duelRepository.save(duelEntity);

        event.getOption(PLAYER_OPTION).getAsUser()
                .openPrivateChannel()
                .queue((channel) -> channel.sendMessage(Message.FT7_CHALLENGE_RECEIVED
                                .formatted(challenger.getUsername()))
                        .setActionRow(
                                Button.primary(Buttons.ACCEPTED_BUTTON + Buttons.SEPARATOR
                                        + savedDuel.getNumId(), "Accept"),
                                Button.danger(Buttons.REJECTED_BUTTON + Buttons.SEPARATOR
                                        + savedDuel.getNumId(), "Reject")
                        ).queue());
        event.reply(Message.FT7_CHALLENGE_SENT.formatted(opponent.getUsername())).setEphemeral(true).queue();
    }
}
