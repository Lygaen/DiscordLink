package fr.lygaen.discord.utils;

import fr.lygaen.discord.DiscordLink;
import fr.lygaen.discord.events.DiscordEvents;
import fr.lygaen.discord.events.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscordUtils {
    public static final List<LinkedVoiceChannel> VOICE_CHANNELS = new ArrayList<>();
    private static final HashMap<Player, Long> PLAYER_TO_DISCORD_ID = new HashMap<>();
    public static final EventWaiter EVENT_WAITER = new EventWaiter();
    public static Guild MAIN_GUILD;
    public static JDA JDA;

    public static boolean hasPlayerLinked(Player p) {
        return PLAYER_TO_DISCORD_ID.containsKey(p);
    }

    public static void addPlayerLink(Player p, User u) {
        PLAYER_TO_DISCORD_ID.put(p, u.getIdLong());
    }

    @Nullable
    public static RestAction<Member> getMemberByPlayer(Player p) {
        return hasPlayerLinked(p) ? MAIN_GUILD.retrieveMemberById(PLAYER_TO_DISCORD_ID.get(p), true) : null;
    }

    public static void loadJDA() {
        disableJDALogging();
        try {
            JDA = JDABuilder.create(DiscordLink.CONFIG.getString("token"),
                            GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.GUILD_VOICE_STATES)
                    .addEventListeners(new DiscordEvents(), EVENT_WAITER)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void loadChannels() {
        ConfigurationSection channels = DiscordLink.CONFIG.getConfigurationSection("channels");
        for (String key : channels.getKeys(false)) {
            ConfigurationSection section = channels.getConfigurationSection(key);
            VOICE_CHANNELS.add(new LinkedVoiceChannel(key,
                    section.getString("display"),
                    MAIN_GUILD.getVoiceChannelById(section.getLong("id"))));
        }
    }

    private static void disableJDALogging() {
        boolean serverIsLog4j21Capable = false;

        try {
            serverIsLog4j21Capable = Class.forName("org.apache.logging.log4j.core.Filter") != null;
        } catch (ClassNotFoundException e) {
            /**/
        }

        // add log4j filter for JDA messages
        if (serverIsLog4j21Capable) {
            try {
                ((Logger) LogManager.getRootLogger()).addFilter(new JDAFilter());
            } catch (Exception e) {
                /**/
            }
        }
    }

    public static void removePlayerLink(Player p) {
        PLAYER_TO_DISCORD_ID.remove(p);
    }

    public static LinkedVoiceChannel getChannelFromName(String channel) {
        return VOICE_CHANNELS.parallelStream().filter((l) -> l.getName().equals(channel)).findAny().orElse(null);
    }

    public static boolean hasChannelFromName(String name) {
        return getChannelFromName(name) != null;
    }

    public static class LinkedVoiceChannel {
        private final String name;
        private final String displayName;
        private final VoiceChannel actualChannel;

        public LinkedVoiceChannel(String name, String displayName, VoiceChannel actualChannel) {
            this.name = name;
            this.displayName = displayName;
            this.actualChannel = actualChannel;
        }

        public VoiceChannel getActualChannel() {
            return actualChannel;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getName() {
            return name;
        }
    }
}
