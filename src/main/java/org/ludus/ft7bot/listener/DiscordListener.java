package org.ludus.ft7bot.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.ludus.ft7bot.command.Command;
import org.ludus.ft7bot.constant.Buttons;
import org.ludus.ft7bot.entity.DuelResultEntity;
import org.ludus.ft7bot.model.DuelStatus;
import org.ludus.ft7bot.repository.DuelResultRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DiscordListener extends ListenerAdapter  {
    private final DuelResultRepository duelResultRepository;
    private final Map<String, Command> commands;

    public DiscordListener(DuelResultRepository duelResultRepository, List<Command> commands) {
        this.duelResultRepository = duelResultRepository;
        this.commands = commands.stream().collect(Collectors.toMap(Command::getName, Function.identity()));
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        for(Guild guild : event.getJDA().getGuilds()) {
            for(Command command : commands.values()) {
                if(command.getOptions() == null) {
                    guild.upsertCommand(command.getName(), command.getDescription()).queue();
                } else {
                    guild.upsertCommand(command.getName(), command.getDescription()).addOptions(command.getOptions()).queue();
                }
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (commands.containsKey(event.getName())) {
            commands.get(event.getName()).execute(event);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] parts = event.getButton().getId().split(Buttons.SEPARATOR);
        String action = parts[0];
        if (action.equals(Buttons.ACCEPTED_BUTTON)) {
            updateChallengeStatus(parts[1], DuelStatus.ACCEPTED);
            event.reply("You have accepted the challenge!").setEphemeral(true).queue();
        } else if (action.equals(Buttons.REJECTED_BUTTON)) {
            updateChallengeStatus(parts[1], DuelStatus.CANCELLED);
            event.reply("You have rejected the challenge.").setEphemeral(true).queue();
        }
    }

    private void updateChallengeStatus(String duelId, DuelStatus status) {
        DuelResultEntity duelResult = duelResultRepository.findById(Long.parseLong(duelId)).orElseThrow();
        duelResult.setDuelStatus(status);
        duelResultRepository.save(duelResult);
    }
}
