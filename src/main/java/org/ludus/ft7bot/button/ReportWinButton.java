package org.ludus.ft7bot.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.ludus.ft7bot.constant.Buttons;
import org.ludus.ft7bot.constant.Message;
import org.ludus.ft7bot.entity.DuelEntity;
import org.ludus.ft7bot.model.DuelStatus;
import org.ludus.ft7bot.repository.DuelRepository;
import org.ludus.ft7bot.service.DuelService;
import org.ludus.ft7bot.util.MessageUtil;
import org.springframework.stereotype.Component;

@Component
public class ReportWinButton implements ButtonAction {
    private final DuelRepository duelRepository;
    private final DuelService duelService;

    public ReportWinButton(DuelRepository duelRepository, DuelService duelService) {
        this.duelRepository = duelRepository;
        this.duelService = duelService;
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        String reporterId = event.getUser().getId();
        String duelId = event.getButton().getId().split(Buttons.SEPARATOR)[1];
        DuelEntity duelEntity = duelRepository.findById(Long.parseLong(duelId)).orElseThrow();
        if (DuelStatus.CANCELLED.equals(duelEntity.getStatus()) || DuelStatus.FINISHED.equals(duelEntity.getStatus())) {
            event.reply(Message.DUEL_FINISHED_OR_CANCELLED).setEphemeral(true).queue();
            MessageUtil.clearButtons(event);
            return;
        }
        String opponentId = reporterId.equals(duelEntity.getChallenger().getDiscordId()) ? duelEntity.getOpponent().getDiscordId() : duelEntity.getChallenger().getDiscordId();
        MessageUtil.clearButtons(event);
        event.reply(duelService.reportWinner(event, duelEntity, reporterId, opponentId, reporterId)).setEphemeral(true).queue();
    }

    @Override
    public String getAction() {
        return Buttons.REPORT_WIN_BUTTON;
    }
}
