package org.ludus.ft7bot.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.ludus.ft7bot.button.ButtonAction;
import org.ludus.ft7bot.constant.Buttons;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ButtonListener extends ListenerAdapter {
    private final Map<String, ButtonAction> buttons;

    public ButtonListener(List<ButtonAction> buttons) {
        this.buttons = buttons.stream().collect(Collectors.toMap(ButtonAction::getAction, Function.identity()));
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getButton().getId() != null) {
            String[] parts = event.getButton().getId().split(Buttons.SEPARATOR);
            String action = parts[0];
            if (buttons.containsKey(action)) {
                buttons.get(action).execute(event);
            }
        }
    }
}
