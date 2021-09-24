package fr.lygaen.discord.commands;

import fr.lygaen.discord.utils.DiscordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChannelsCommand extends BaseCommand {
    public ChannelsCommand() {
        super("channels");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) return false;

        Player p = ((Player) sender);
        p.sendMessage("§9Here are all of the channels : §r" +
                DiscordUtils.VOICE_CHANNELS
                        .parallelStream()
                        .map(linkedVoiceChannel -> linkedVoiceChannel.getDisplayName()+"§r, ")
                        .reduce("", (s1, s2) -> s1 + s2));
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
