package me.spec.eris.client.commands;

import me.spec.eris.Eris;
import me.spec.eris.api.command.Command;
import me.spec.eris.utils.player.PlayerUtils;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "gives all commands");
    }

    @Override
    public void execute(String[] commandArguments) {
        Eris.getInstance().commandManager.getManagerArraylist().forEach(this::getCommandNameAndDescription);
    }

    private void getCommandNameAndDescription(Command command) {
        if(!command.getCommandName().equalsIgnoreCase("help")) {
            PlayerUtils.tellUser(" " + command.getCommandName() + " - " + command.getCommandDescription());
        }
    }
}
