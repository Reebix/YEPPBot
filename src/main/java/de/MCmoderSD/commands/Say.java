package de.MCmoderSD.commands;

import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import de.MCmoderSD.core.CommandHandler;

import de.MCmoderSD.utilities.database.MySQL;

import java.util.ArrayList;

import static de.MCmoderSD.utilities.other.Calculate.*;

public class Say {

    // Constructor
    public Say(MySQL mySQL, CommandHandler commandHandler, TwitchChat chat, ArrayList<String> admins) {

        // About
        String[] name = {"say", "repeat"};
        String description = "Nur für Administratoren. Sendet eine Nachricht in den Chat. Verwendung: " + commandHandler.getPrefix() + "say <Nachricht>";


        // Register command
        commandHandler.registerCommand(new Command(description, name) {
            @Override
            public void execute(ChannelMessageEvent event, String... args) {
                String channel = getChannel(event);
                String author = getAuthor(event);

                String response;
                if (admins.contains(author) || author.equals(channel)) response = processArgs(args);
                else return;

                // Send Message and log
                chat.sendMessage(channel, response);
                mySQL.logResponse(event, getCommand(), processArgs(args), response);
            }
        });
    }
}