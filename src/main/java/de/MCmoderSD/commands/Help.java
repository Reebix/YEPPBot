package de.MCmoderSD.commands;

import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import de.MCmoderSD.core.CommandHandler;
import de.MCmoderSD.utilities.json.JsonNode;

import java.util.Arrays;
import java.util.HashMap;

import static de.MCmoderSD.utilities.Calculate.getChannel;

public class Help {

    // Attributes
    private final CommandHandler commandHandler;
    private final JsonNode whitelist;
    private final JsonNode blacklist;

    // Constructor
    public Help(CommandHandler commandHandler, TwitchChat chat, JsonNode whitelist, JsonNode blacklist) {

        // Description
        String description = "Um die verfügbaren Befehle zu sehen, schreibe: " + commandHandler.getPrefix() + "help commands. Um ein hilfe bei einem Befehl zu erhalten, schreibe: " + commandHandler.getPrefix() + "help <Befehl>.";


        // Init Attributes
        this.commandHandler = commandHandler;
        this.whitelist = whitelist;
        this.blacklist = blacklist;

        // Register command
        commandHandler.registerCommand(new Command(description, "help", "hilfe") { // Command name and aliases
            @Override
            public void execute(ChannelMessageEvent event, String... args) {
                String channel = getChannel(event);
                String arg = args.length > 0 ? args[0].toLowerCase() : "";

                // Help Commands
                if (arg.equals("commands") || arg.equals("command") || arg.equals("befehle") || arg.equals("befehl")) chat.sendMessage(channel, helpCommands(channel)); // Help Commands
                else if (getCommandDescription(channel, arg) != null) chat.sendMessage(channel, getCommandDescription(channel, arg)); // Command Description
                else chat.sendMessage(channel, getDescription()); // Help Description (Default)
            }
        });
    }

    // Gets all available commands
    private String helpCommands(String channel) {
        StringBuilder message = new StringBuilder("Die verfügbaren Befehle sind: ");

        // Get prefix
        String prefix = commandHandler.getPrefix();

        // Get commands
        HashMap<String, Command> commands = commandHandler.getCommands();

        // Filter available commands
        for (String command : commands.keySet()) {
            if (whitelist.containsKey(command.toLowerCase())) {
                if (Arrays.stream(whitelist.get(command.toLowerCase()).asText().toLowerCase().split("; ")).toList().contains(channel)) message.append(prefix).append(command).append(", ");
            } else if (blacklist.containsKey(command.toLowerCase())) {
                if (!Arrays.stream(blacklist.get(command.toLowerCase()).asText().toLowerCase().split("; ")).toList().contains(channel)) message.append(prefix).append(command).append(", ");
            } else message.append(prefix).append(command).append(", ");
        }

        // Return message
        return message.substring(0, message.length() - 2) + '.';
    }

    // Gets the description of a command
    private String getCommandDescription(String channel, String command) {

        // Get command
        if (commandHandler.getAliases().containsKey(command)) command = commandHandler.getAliases().get(command);

        // Get description if command is available
        if (whitelist.containsKey(command.toLowerCase())) {
            if (Arrays.stream(whitelist.get(command.toLowerCase()).asText().toLowerCase().split("; ")).toList().contains(channel)) return commandHandler.getCommands().get(command).getDescription();
        } else if (blacklist.containsKey(command.toLowerCase())) {
            if (!Arrays.stream(blacklist.get(command.toLowerCase()).asText().toLowerCase().split("; ")).toList().contains(channel)) return commandHandler.getCommands().get(command).getDescription();
        } else if (commandHandler.getCommands().containsKey(command)) return commandHandler.getCommands().get(command).getDescription();
        return null;
    }
}