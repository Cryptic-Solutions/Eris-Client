package me.spec.eris.client.commands;

import me.spec.eris.Eris;
import me.spec.eris.api.command.Command;

public class ToggleCommand extends Command {


    public ToggleCommand() {
        super("toggle", "toggle <module>");
    }

    @Override
    public void execute(String[] commandArguments) {
        if(commandArguments.length == 2) {
            if(Eris.getInstance().moduleManager.getModuleByName(commandArguments[1]) != null) {
                Eris.getInstance().moduleManager.getModuleByName(commandArguments[1]).toggle(true);
                Eris.getInstance().tellUser("Toggled " + commandArguments[1]);
            } else {
                Eris.getInstance().tellUser("That module doesnt exist!");
            }
         } else if(commandArguments.length < 2 || commandArguments.length > 2) {
            Eris.getInstance().tellUser("Invalid command arguments!");
        }
    }
}
