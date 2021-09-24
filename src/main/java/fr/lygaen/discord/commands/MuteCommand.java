package fr.lygaen.discord.commands;

import fr.lygaen.discord.utils.DiscordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class MuteCommand extends BaseCommand {
    public MuteCommand() {
        super("mute");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) return false;
        Player p;
        if(args.length == 0) {
            p = ((Player) sender);
        } else if(args.length == 1) {
            p = Bukkit.getPlayerExact(args[0]);
        } else {
            return false;
        }

        return mute(p);
    }

    private boolean mute(Player p) {
        if(!DiscordUtils.hasPlayerLinked(p)) {
            p.sendMessage("§c"+p.getName()+" is not linked yet !");
            return true;
        }


        //noinspection ConstantConditions
        DiscordUtils.getMemberByPlayer(p).queue((m) -> {
            boolean muted = m.getVoiceState().isMuted();
            m.mute(!muted).queue();
            p.sendMessage("§a"+p.getName()+" ("+m.getEffectiveName()+") was "+(muted ? "un" : "")+"muted !");
        }, (t) -> {
            p.sendMessage("§cCould not fetch "+p.getName()+" from discord :/");
        });
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Bukkit.getOnlinePlayers().parallelStream().map(HumanEntity::getName).collect(Collectors.toList());
    }
}
