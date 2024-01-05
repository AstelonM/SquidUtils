package com.astelon.squidutils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DiscordUtils {

    public static final long DISCORD_EPOCH = 1420070400000L;

    public static void tryDelete(Message message) {
        if (message.isFromType(ChannelType.TEXT)) {
            TextChannel channel = message.getTextChannel();
            if (message.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE))
                message.delete().queue();
        }
    }

    public static long parseSnowflake(String text) throws IllegalArgumentException {
        if (text == null || text.isEmpty())
            throw new IllegalArgumentException("The snowflake text cannot be empty");
        try {
            if (text.startsWith("-"))
                return Long.parseLong(text);
            else
                return Long.parseUnsignedLong(text);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("The format of the text isn't a proper long");
        }
    }

    public static String creationTime(long id) {
        id = id >> 22;
        id += DISCORD_EPOCH;
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(id), ZoneId.systemDefault());
        return time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss"));
    }

    public static long creationTimeMilli(long id) {
        id = id >> 22;
        id += DISCORD_EPOCH;
        return id;
    }
}
