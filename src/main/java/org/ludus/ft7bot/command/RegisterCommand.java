package org.ludus.ft7bot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.ludus.ft7bot.constant.CommandName;
import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.constant.OptionName;
import org.ludus.ft7bot.entity.PlayerEntity;
import org.ludus.ft7bot.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegisterCommand implements Command {
    private static final int MAX_USERNAME_LENGTH = 100;
    private static final Logger LOG = LoggerFactory.getLogger(RegisterCommand.class);
    private final PlayerRepository playerRepository;

    public RegisterCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return CommandName.REGISTER;
    }

    @Override
    public String getDescription() {
        return "Register a new player";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING,
                OptionName.USERNAME_OPTION,
                "The name to be used in the duelling leaderboard",
                true).setMaxLength(MAX_USERNAME_LENGTH));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            final String discordId = event.getUser().getId();
            if (playerRepository.existsByDiscordId(discordId)) {
                event.reply(Message.DISCORD_ID_ALREADY_REGISTERED).setEphemeral(true).queue();
                return;
            }

            final String username = event.getOption(OptionName.USERNAME_OPTION).getAsString();
            if (playerRepository.existsByUsername(username)) {
                event.reply(Message.USERNAME_ALREADY_REGISTERED).setEphemeral(true).queue();
                return;
            }

            final PlayerEntity playerEntity = new PlayerEntity();
            playerEntity.setDiscordId(discordId);
            playerEntity.setUsername(username);
            playerRepository.save(playerEntity);
            event.reply(Message.SUCCESSFUL_REGISTRATION.formatted(username)).setEphemeral(true).queue();
        } catch (Exception e) {
            LOG.error(Message.REGISTRATION_UNEXPECTED_ERROR, e);
            event.reply(Message.REGISTRATION_UNEXPECTED_ERROR).setEphemeral(true).queue();
        }
    }
}
