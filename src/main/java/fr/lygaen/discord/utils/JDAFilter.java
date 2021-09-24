package fr.lygaen.discord.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

public class JDAFilter implements Filter {
    public Result check(String loggerName, Level level, String message, Throwable throwable) {
        return loggerName.startsWith("net.dv8tion.jda") ? Result.DENY : Result.NEUTRAL;
    }

    @Override
    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(LogEvent logEvent) {
        return check(
                logEvent.getLoggerName(),
                logEvent.getLevel(),
                logEvent.getMessage()
                        .getFormattedMessage(),
                logEvent.getThrown());
    }
    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object... parameters) {
        return check(
                logger.getName(),
                level,
                message,
                null);
    }
    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object message, Throwable throwable) {
        return check(
                logger.getName(),
                level,
                message.toString(),
                throwable);
    }
    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message message, Throwable throwable) {
        return check(
                logger.getName(),
                level,
                message.getFormattedMessage(),
                throwable);
    }
}
