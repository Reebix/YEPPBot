package de.MCmoderSD.commands;

import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import de.MCmoderSD.core.CommandHandler;

import static de.MCmoderSD.utilities.Calculate.getAuthor;
import static de.MCmoderSD.utilities.Calculate.tagAuthor;

public class JoinChat {

    // Constructor
    public JoinChat(CommandHandler commandHandler, TwitchChat chat, String[] admins) {

        // Register command
        commandHandler.registerCommand(new Command("joinchat", "addchat", "addtochat") { // Command name and aliases
            @Override
            public void execute(ChannelMessageEvent event, String... args) {
                if (tagAuthor(event).equals(event.getChannel().getName()))
                    join(event, chat, event.getChannel().getName());
                else for (String admin : admins)
                    if (getAuthor(event).equals(admin.toLowerCase())) join(event, chat, args[0]);
            }
        });
    }

    // Join chat
    private void join(ChannelMessageEvent event, TwitchChat chat, String... args) {
        chat.sendMessage(event.getChannel().getName(), "Joining " + args[0]);
        chat.joinChannel(args[0]);
    }
}