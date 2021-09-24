package fr.lygaen.discord.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;

public abstract class BaseCommand implements CommandExecutor, TabExecutor {
    private final String command;

    protected BaseCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
