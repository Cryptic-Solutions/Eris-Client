package me.spec.eris.command;

public abstract class Command {

    private String commandName;

    public Command(String commandName) {
        this.commandName = commandName;
    }

    public abstract void execute(String[] commandArguments);

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }
}
