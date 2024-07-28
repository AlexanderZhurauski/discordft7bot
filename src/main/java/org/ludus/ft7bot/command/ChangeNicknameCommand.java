package org.ludus.ft7bot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.ludus.ft7bot.constant.CommandName;
import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.constant.OptionName;
import org.ludus.ft7bot.entity.PlayerEntity;
import org.ludus.ft7bot.repository.PlayerRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChangeNicknameCommand implements Command {
    private final PlayerRepository playerRepository;

    public ChangeNicknameCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return CommandName.CHANGE_NICKNAME;
    }

    @Override
    public String getDescription() {
        return "Change your username";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING,
                OptionName.USERNAME_OPTION,
                "The name to be used in the duelling leaderboard",
                true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String discordId = event.getUser().getId();
        if (!playerRepository.existsByDiscordId(discordId)) {
            event.reply(Message.CHALLENGER_NOT_REGISTERED).setEphemeral(true).queue();
        }
        PlayerEntity playerEntity = playerRepository.findByDiscordId(discordId);
        String oldUsername = playerEntity.getUsername();
        playerEntity.setUsername(event.getOption(OptionName.USERNAME_OPTION).getAsString());
        playerRepository.save(playerEntity);
        event.reply(Message.SUCCESSFUL_USERNAME_CHANGE.formatted(oldUsername, playerEntity.getUsername())).setEphemeral(true).queue();
    }
}
