package de.MCmoderSD.commands;

import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import de.MCmoderSD.core.CommandHandler;

import de.MCmoderSD.utilities.database.MySQL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static de.MCmoderSD.utilities.other.Calculate.*;

public class CustomCommand {

        // Constructor
        public CustomCommand(MySQL mySQL, CommandHandler commandHandler, TwitchChat chat, ArrayList<String> admins) {

            String prefix = commandHandler.getPrefix();
            String syntax = prefix + "CustomCommand create/enable/disable/delete name/alias";

            // About
            String[] name = {"customcommand", "cc"};
            String description = "Kann benutzt werden um eigene Commands zu erstellen. Syntax: " + syntax;

            // Register command
            commandHandler.registerCommand(new Command(description, name) {
                @Override
                public void execute(ChannelMessageEvent event, String... args) {

                    // Get author and channel
                    String author = getAuthor(event);
                    String channel = getChannel(event);

                    // Check if user is a moderator
                    if (!(channel.equals(author) || admins.contains(author))) return;

                    // Get command syntax
                    HashMap<String, Integer> message = new HashMap<>();
                    for (var i = 1; i < args.length; i++) message.put(args[i], i);

                    String response;
                    if (args.length < 2) response = "Syntax: " + syntax;
                    else if (Arrays.asList("create", "add", "enable", "disable", "delete", "del", "remove", "rm").contains(args[0].toLowerCase())){

                        // Process command
                        String commandName = trimMessage(args[1].toLowerCase());
                        commandName = commandName.startsWith(prefix) ? commandName.substring(prefix.length()) : commandName;

                        // Process message
                        switch (args[0].toLowerCase()) {

                                    // Enable command
                                case "enable":
                                    response = mySQL.editCommand(event, commandName, true);
                                    break;

                                    // Disable command
                                case "disable":
                                    response = mySQL.editCommand(event, commandName, false);
                                    break;

                                    // Delete command
                                case "delete":
                                case "del":
                                case "remove":
                                case "rm":
                                    response = mySQL.deleteCommand(event, commandName);
                                    break;

                                    // Create command
                                case "create":
                                case "add":
                                    if (!message.containsKey(":")) response = "Syntax: " + syntax;
                                    else if (mySQL.getCommands(event, true).contains(commandName) || mySQL.getAliases(event, true).containsKey(commandName)) response = "Command already exists";
                                    else {

                                        // Get command aliases
                                        ArrayList<String> aliases = new ArrayList<>(Arrays.asList(args).subList(message.get(":") + 1, args.length));
                                        aliases.replaceAll(String::toLowerCase);

                                        // Get command response
                                        StringBuilder stringBuilder = new StringBuilder();
                                        for (var i = message.get(":") + 1; i < args.length; i++) stringBuilder.append(args[i]).append(" ");
                                        String commandResponse = trimMessage(stringBuilder.toString());

                                        // Create command
                                        response = mySQL.createCommand(event, commandName, aliases, commandResponse);
                                    }
                                    break;

                                    // Error
                                default:
                                    response = "Syntax: " + syntax;
                        }
                    } else response = "Syntax: " + syntax;

                    // Send message
                    chat.sendMessage(channel, response);

                    // Log response
                    mySQL.logResponse(event, getCommand(), processArgs(args), response);
                }
            });
        }
}