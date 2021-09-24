package fr.lygaen.discord.commands;

import fr.lygaen.discord.utils.DiscordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnlinkCommand extends BaseCommand {
    public UnlinkCommand() {
        super("unlink");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) return false;

        Player p = ((Player) sender);

        if(!DiscordUtils.hasPlayerLinked(p)) {
            p.sendMessage("§cYou are not linked yet ! Link with /link");
            return true;
        }

        DiscordUtils.removePlayerLink(p);
        p.sendMessage("§aYou minecraft account is now unlinked from your discord account !");

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
