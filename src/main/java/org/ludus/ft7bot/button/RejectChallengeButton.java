package org.ludus.ft7bot.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.ludus.ft7bot.constant.Buttons;
import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.model.DuelStatus;
import org.ludus.ft7bot.service.DuelService;
import org.springframework.stereotype.Component;

@Component
public class RejectChallengeButton implements ButtonAction {
    private final DuelService duelService;

    public RejectChallengeButton(DuelService duelService) {
        this.duelService = duelService;
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        String duelId = event.getButton().getId().split(Buttons.SEPARATOR)[1];
        duelService.updateChallengeStatus(event, duelId, DuelStatus.CANCELLED);
        event.reply(Message.FT7_REJECTED_BY_YOURSELF).setEphemeral(true).queue();
    }

    @Override
    public String getAction() {
        return Buttons.REJECTED_BUTTON;
    }
}
