package org.ludus.ft7bot.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface ButtonAction {
    void execute(ButtonInteractionEvent event);
    String getAction();
}
