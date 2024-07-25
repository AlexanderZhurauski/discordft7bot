package org.ludus.ft7bot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.ludus.ft7bot.constant.Buttons;
import org.ludus.ft7bot.entity.DuelResultEntity;
import org.ludus.ft7bot.entity.PlayerEntity;
import org.ludus.ft7bot.model.DuelStatus;
import org.ludus.ft7bot.repository.DuelResultRepository;
import org.ludus.ft7bot.repository.PlayerRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class Ft7Command implements Command {
    private static final String PLAYER_OPTION = "player";
    private final DuelResultRepository duelResultRepository;
    private final PlayerRepository playerRepository;

    public Ft7Command(DuelResultRepository duelResultRepository, PlayerRepository playerRepository) {
        this.duelResultRepository = duelResultRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return "ft7";
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
            event.reply("Please register in the ft7 system using the '/register <username>' command.").setEphemeral(true).queue();
            return;
        }

        final String opponentId = event.getOption(PLAYER_OPTION).getAsUser().getId();
        if (!playerRepository.existsByDiscordId(opponentId)) {
            event.reply("The player you have challenged has not registered in the ft7 system.").setEphemeral(true).queue();
            return;
        }

        if (challengerId.equals(opponentId)) {
            event.reply("No, you can't challenge yourself to a ft7 :).").setEphemeral(true).queue();
            return;
        }

        final PlayerEntity challenger = playerRepository.findByDiscordId(challengerId);
        final PlayerEntity opponent = playerRepository.findByDiscordId(opponentId);

        final DuelResultEntity duelResultEntity = new DuelResultEntity();
        duelResultEntity.setChallenger(challenger);
        duelResultEntity.setOpponent(opponent);
        duelResultEntity.setDuelStatus(DuelStatus.PENDING);
        final DuelResultEntity savedDuelResultEntity = duelResultRepository.save(duelResultEntity);

        event.getOption(PLAYER_OPTION).getAsUser()
                .openPrivateChannel()
                .queue((channel) -> channel.sendMessage("You have been challenged to a ft7 by %s, would you like to accept?"
                .formatted(challenger.getUsername()))
                        .setActionRow(
                                Button.primary(Buttons.ACCEPTED_BUTTON + Buttons.SEPARATOR + savedDuelResultEntity.getNumId(), "Accept"),
                                Button.danger(Buttons.REJECTED_BUTTON + Buttons.SEPARATOR + savedDuelResultEntity.getNumId(), "Reject")
                        ).queue());
    }
}
