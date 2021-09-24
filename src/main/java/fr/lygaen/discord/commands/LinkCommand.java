package fr.lygaen.discord.commands;

import fr.lygaen.discord.utils.DiscordUtils;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LinkCommand extends BaseCommand {
    public static final List<UUID> LINKING_PLAYERS = new ArrayList<>();
    private static final char[] POSSIBLE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final SecureRandom RNG = new SecureRandom();

    public LinkCommand() {
        super("link");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player) || DiscordUtils.hasPlayerLinked(((Player) sender))) return false;

        Player p = ((Player) sender);

        if(LINKING_PLAYERS.contains(p.getUniqueId())) {
            p.sendMessage("§cYou are already linking your account ! Please finish your current linking.");
            return true;
        }

        String id = generateRandomString();

        p.sendMessage("§9Please send a message to the discord bot " + getBotUsername() + " with the following id : §e" + id);
        LINKING_PLAYERS.add(p.getUniqueId());
        DiscordUtils.EVENT_WAITER.waitForEvent(PrivateMessageReceivedEvent.class,
                (m) -> m.getMessage().getContentRaw().equals(id),
                (m) -> {
                    DiscordUtils.addPlayerLink(p, m.getAuthor());
                    LINKING_PLAYERS.remove(p.getUniqueId());
                    m.getChannel().sendMessage("You are now linked with your minecraft account !").queue();
                    p.sendMessage("§aYou are now linked with your discord account !");
        }, 60L, TimeUnit.SECONDS, () -> {
            p.sendMessage("§cYou timed out (limit of 1 minute) !");
            LINKING_PLAYERS.remove(p.getUniqueId());
        });

        return true;
    }

    private String generateRandomString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            builder.append(POSSIBLE_CHARS[RNG.nextInt(POSSIBLE_CHARS.length)]);
        }

        return builder.toString();
    }

    private String getBotUsername() {
        return DiscordUtils.JDA.getSelfUser().getAsTag();
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
