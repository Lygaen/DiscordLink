package fr.lygaen.discord.events;

import fr.lygaen.discord.DiscordLink;
import fr.lygaen.discord.utils.DiscordUtils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiscordEvents extends ListenerAdapter {
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        if(event.getGuild().getIdLong() == DiscordLink.CONFIG.getLong("guild-id")) {
            DiscordUtils.MAIN_GUILD = event.getGuild();
            DiscordUtils.loadChannels();
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ConfigurationSection presence = DiscordLink.CONFIG.getConfigurationSection("presence");
        Activity activity;
        OnlineStatus status;

        if(presence == null) {
            status = parseStatus("");
            activity = createActivity(null);
        } else {
            status = parseStatus(presence.getString("status", ""));
            activity = createActivity(presence);
        }

        event.getJDA().getPresence().setPresence(status, activity);
    }

    private Activity createActivity(@Nullable ConfigurationSection section) {
        Activity activity;
        String text = "the Minecraft Server !";
        String type = "";

        if(section != null) {
            text = section.getString("text", "");
            type = section.getString("type", "");
        }

        switch (type) {
            case "listening":
                activity = Activity.listening(text);
                break;
            case "playing":
                activity = Activity.playing(text);
                break;
            case "competing":
                activity = Activity.competing(text);
                break;
            case "streaming":
                String url = section.getString("url");

                if(!Activity.isValidStreamingUrl(url)) {
                    activity = Activity.watching(text);
                } else {
                    activity = Activity.streaming(text, url);
                }
                break;
            default:
                activity = Activity.watching(text);
                break;
        }

        return activity;
    }

    private OnlineStatus parseStatus(String key) {
        OnlineStatus status;

        switch (key) {
            case "dnd":
                status = OnlineStatus.DO_NOT_DISTURB;
                break;
            case "idle":
                status = OnlineStatus.IDLE;
                break;
            case "offline":
                status = OnlineStatus.OFFLINE;
                break;
            default:
                status = OnlineStatus.ONLINE;
                break;
        }

        return status;
    }
}
