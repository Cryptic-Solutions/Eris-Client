package me.spec.eris.command.impl;

import me.spec.eris.Eris;
import me.spec.eris.command.Command;
import me.spec.eris.module.Module;
import me.spec.eris.ui.click.ClickGui;

import java.awt.*;
import java.awt.event.KeyEvent;

public class HelpCommand extends Command {

    private boolean cancelMessage;

    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commandArguments) {
        Eris.getInstance().modules.getModules().forEach(module -> Eris.getInstance().tellUser("Press RSHIFT to open ClickUI"));
    }
}
