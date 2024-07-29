package org.ludus.ft7bot.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.ludus.ft7bot.constant.Buttons;
import org.ludus.ft7bot.entity.DuelEntity;
import org.ludus.ft7bot.repository.DuelRepository;
import org.ludus.ft7bot.service.DuelService;
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
        String opponentId = reporterId.equals(duelEntity.getChallenger().getDiscordId()) ? duelEntity.getOpponent().getDiscordId() : reporterId;
        event.getMessage().editMessageComponents().queue();
        event.reply(duelService.reportWinner(duelEntity, reporterId, opponentId, reporterId)).setEphemeral(true).queue();
    }

    @Override
    public String getAction() {
        return Buttons.REPORT_WIN_BUTTON;
    }
}
