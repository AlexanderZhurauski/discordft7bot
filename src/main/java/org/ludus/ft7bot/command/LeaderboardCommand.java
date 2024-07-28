package org.ludus.ft7bot.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.ludus.ft7bot.constant.CommandName;
import org.ludus.ft7bot.entity.PlayerEntity;
import org.ludus.ft7bot.repository.PlayerRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeaderboardCommand implements Command {
    private final PlayerRepository playerRepository;

    public LeaderboardCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return CommandName.LEADERBOARD;
    }

    @Override
    public String getDescription() {
        return "Show the current player leaderboard";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Leaderboard");
        eb.setColor(0xF1C40F);

        for (PlayerEntity player : playerRepository.findAllByOrderByEloDesc()) {
            eb.addField(player.getUsername(), "Rating: " + player.getElo(), false);
        }

        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }
}
