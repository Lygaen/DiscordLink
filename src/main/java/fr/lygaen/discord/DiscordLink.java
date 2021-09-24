package fr.lygaen.discord;

import fr.lygaen.discord.commands.*;
import fr.lygaen.discord.utils.DiscordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiscordLink extends JavaPlugin {
    public static FileConfiguration CONFIG;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        CONFIG = getConfig();

        registerCommand(new LinkCommand());
        registerCommand(new MoveCommand());
        registerCommand(new UnlinkCommand());
        registerCommand(new ChannelsCommand());
        registerCommand(new MuteCommand());
        registerCommand(new DeafenCommand());

        DiscordUtils.loadJDA();
    }

    @Override
    public void onDisable() {
        DiscordUtils.JDA.shutdownNow();
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommand(BaseCommand command) {
        PluginCommand pluginCommand = Bukkit.getPluginCommand(command.getCommand());
        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);
    }
}
