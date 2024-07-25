package org.ludus.ft7bot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.ludus.ft7bot.entity.PlayerEntity;
import org.ludus.ft7bot.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegisterCommand implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(RegisterCommand.class);
    private static final String USERNAME_OPTION = "username";
    private PlayerRepository playerRepository;

    public RegisterCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "Register a new player";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING,
                USERNAME_OPTION,
                "The name to be used in the duelling leaderboard",
                true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            String discordId = event.getUser().getId();
            if (playerRepository.existsByDiscordId(discordId)) {
                event.reply("A player with this discord id has already been registered.").setEphemeral(true).queue();
            }

            String username = event.getOption(USERNAME_OPTION).getAsString();
            if (playerRepository.existsByUsername(username)) {
                event.reply("A player with this username has already been registered.").setEphemeral(true).queue();
            }

            PlayerEntity playerEntity = new PlayerEntity();
            playerEntity.setDiscordId(discordId);
            playerEntity.setUsername(username);
            playerRepository.save(playerEntity);
            event.reply(username + " has successfully been registered!").setEphemeral(true).queue();
        } catch (Exception e) {
            LOG.error("Unexpected error during registration:", e);
            event.reply("Unexpected error during registration, please contact the server administrator.").setEphemeral(true).queue();
        }
    }
}
