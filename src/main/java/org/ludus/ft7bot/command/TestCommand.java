package org.ludus.ft7bot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestCommand implements Command {
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getDescription() {
        return "Just trying things out";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.getChannel().sendMessage("Hello there!").queue();
    }
}
