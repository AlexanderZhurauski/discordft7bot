package org.ludus.ft7bot.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.ludus.ft7bot.command.Command;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CommandListener extends ListenerAdapter  {
    private final Map<String, Command> commands;

    public CommandListener(List<Command> commands) {
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
}
