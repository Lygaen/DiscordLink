package fr.lygaen.discord.commands;

import fr.lygaen.discord.utils.DiscordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MoveCommand extends BaseCommand {
    public MoveCommand() {
        super("move");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;

        Player p = ((Player) sender);
        String channel;
        if(args.length == 1) {
            channel = args[0];
        } else if(args.length == 2) {
            p = Bukkit.getPlayerExact(args[0]);
            channel = args[1];
        } else {
            return false;
        }


        return move(p, channel);
    }

    public boolean move(Player p, String name) {
        if(!DiscordUtils.hasPlayerLinked(p)) {
            p.sendMessage("§c"+p.getName()+" is not linked yet !");
            return true;
        } else if(!DiscordUtils.hasChannelFromName(name)) {
            p.sendMessage("§cThis channel doesn't exists !");
            return true;
        }

        //Get member returns null only of the player is not linked,
        //which is checked above.
        //noinspection ConstantConditions
        DiscordUtils.getMemberByPlayer(p).queue((m) -> {
            DiscordUtils.LinkedVoiceChannel channel = DiscordUtils.getChannelFromName(name);
            if (m.getVoiceState().getChannel() == null) {
                p.sendMessage("§c"+p.getName()+" ("+m.getEffectiveName()+") is not in a voice channel, cannot move him...");
            } else {
                DiscordUtils.MAIN_GUILD.moveVoiceMember(m, channel.getActualChannel()).queue((v) -> {
                    p.sendMessage("§a"+p.getName()+" ("+m.getEffectiveName()+") was moved to the channel "+channel.getDisplayName()+" §a!");
                }, (t) -> {
                    p.sendMessage("§cCould not move "+p.getName()+" ("+m.getEffectiveName()+") to the channel "+channel.getDisplayName()+" §c:/");
                });
            }
        }, (t) -> {
            p.sendMessage("§cCould not fetch "+p.getName()+" from discord :/");
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if(args.length == 1) {
            List<String> collect = Bukkit.getOnlinePlayers().parallelStream().map(HumanEntity::getName).collect(Collectors.toList());
            collect.addAll(DiscordUtils.VOICE_CHANNELS.parallelStream().map(DiscordUtils.LinkedVoiceChannel::getName).collect(Collectors.toList()));
            return collect;
        } else if(args.length == 2) {
            return DiscordUtils.VOICE_CHANNELS.parallelStream().map(DiscordUtils.LinkedVoiceChannel::getName).collect(Collectors.toList());
        }

        return null;
    }
}
