package me.spec.eris.command.impl;

import me.spec.eris.Eris;
import me.spec.eris.command.Command;

public class ToggleCommand extends Command {


    public ToggleCommand() {
        super("toggle");
    }

    @Override
    public void execute(String[] commandArguments) {
        if(commandArguments.length == 2) {
            if(Eris.getInstance().modules.getModuleByName(commandArguments[1]) != null) {
                Eris.getInstance().modules.getModuleByName(commandArguments[1]).toggle(true);
                Eris.getInstance().tellUser("Toggled " + commandArguments[1]);
            } else {
                Eris.getInstance().tellUser("That module doesnt exist!");
            }
         }
    }
}
