package org.ludus.ft7bot.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.ludus.ft7bot.constant.Buttons;
import org.springframework.stereotype.Component;

@Component
public class CancelDuelButton implements ButtonAction {
    @Override
    public void execute(ButtonInteractionEvent event) {
        //TODO: implement
    }

    @Override
    public String getAction() {
        return Buttons.CANCEL_DUEL;
    }
}
