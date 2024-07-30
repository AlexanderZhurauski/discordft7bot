package org.ludus.ft7bot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.ludus.ft7bot.constant.CommandName;
import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.repository.PlayerRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyRatingCommand implements Command {
    private final PlayerRepository playerRepository;

    public MyRatingCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return CommandName.MY_RATING;
    }

    @Override
    public String getDescription() {
        return "Get your current ELO rating";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply(Message.MY_CURRENT_ELO.formatted(playerRepository.findByDiscordId(event.getUser().getId()).getElo()))
                .setEphemeral(true).queue();
    }
}
