package org.ludus.ft7bot.util;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.ludus.ft7bot.constant.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageUtil {
    private static final Logger LOG = LoggerFactory.getLogger(MessageUtil.class);

    public static void clearButtons(ButtonInteractionEvent event) {
        event.getMessage().editMessageComponents().queue();
    }

    public static void sendByDiscordId(Event event, String discordId, String message, ItemComponent... itemComponents) {
        event.getJDA().retrieveUserById(discordId)
                .queue(user -> user.openPrivateChannel()
                        .flatMap(channel -> itemComponents == null ? channel.sendMessage(message) : channel.sendMessage(message).addActionRow(itemComponents))
                        .queue(), throwable -> LOG.error(Message.USER_RETRIEVAL_FAILED, throwable));
    }
}
