package me.spec.eris.manager.impl;

import me.spec.eris.Eris;
import me.spec.eris.command.Command;
import me.spec.eris.command.impl.HelpCommand;
import me.spec.eris.event.Event;
import me.spec.eris.event.chat.ChatMessageEvent;
import me.spec.eris.manager.Manager;

public class CommandManager extends Manager<Command> {


    @Override
    public void loadManager() {
    addToManagerArraylist(new HelpCommand());
    }

    public void onEvent(Event e) {
        ChatMessageEvent event = (ChatMessageEvent) e;
        String chatMessage = event.getChatMessage();

        if(chatMessage.startsWith(".")) {
            e.setCancelled();
            String noPrefixChatMessage = chatMessage.replace(".", "");
            String[] commandArguments = noPrefixChatMessage.split(" ");
            for(Command command : getManagerArraylist()) {
                if(commandArguments[0].equalsIgnoreCase(command.getCommandName())) {
                    command.execute(commandArguments);
                } else {
                    Eris.getInstance().tellUser("That command doesnt exist!");
                }
            }

        }

    }
}
