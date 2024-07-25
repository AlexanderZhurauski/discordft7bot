package org.ludus.ft7bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.ludus.ft7bot.listener.MyListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BotInitializer implements CommandLineRunner {
    @Value("${bot.token}")
    private String token;
    private final MyListener myListener;

    public BotInitializer(MyListener myListener) {
        this.myListener = myListener;
    }

    @Override
    public void run(String... args) throws Exception {
        JDA jda = JDABuilder.createDefault(token).addEventListeners(myListener).build();
        jda.awaitReady();
    }
}
