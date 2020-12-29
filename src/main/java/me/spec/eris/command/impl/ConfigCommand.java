package me.spec.eris.command.impl;

import me.spec.eris.Eris;
import me.spec.eris.command.Command;

public class ConfigCommand extends Command {


    public ConfigCommand() {
        super("config");
    }

    @Override
    public void execute(String[] commandArguments) {
        Eris.getInstance().tellUser("TODO");
    }
}
