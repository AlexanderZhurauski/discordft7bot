package org.ludus.ft7bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class BotInitializer implements CommandLineRunner {
    @Value("${bot.token}")
    private String token;
    private final Collection<ListenerAdapter> listeners;

    public BotInitializer(Collection<ListenerAdapter> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void run(String... args) throws Exception {
        JDA jda = JDABuilder.createDefault(token)
                .addEventListeners(listeners.toArray())
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
        jda.awaitReady();
    }
}
