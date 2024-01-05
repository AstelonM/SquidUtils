package com.astelon.squidutils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.astelon.squidutils.DiscordUtils.parseSnowflake;

public class GetFromString { //TODO de imbunatatit

    //TODO poate le inlocuiesc cu originalele?
    public static final Pattern USER_PATTERN = Message.MentionType.USER.getPattern();
    public static final Pattern ROLE_PATTERN = Message.MentionType.ROLE.getPattern();

    public static ArrayList<User> getMentionedUsers(JDA jda, String text) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        HashSet<Long> foundIds = new HashSet<>();
        ArrayList<User> users = new ArrayList<>();
        Matcher matcher = USER_PATTERN.matcher(text);
        long id;
        User user;
        while (matcher.find()) {
            try {
                id = parseSnowflake(matcher.group(1));
                if (foundIds.contains(id))
                    continue;
                foundIds.add(id);
                user = jda.getUserById(id);
                if (user != null)
                    users.add(user);
            } catch (NumberFormatException ignored) {}
        }
        return users;
    }

    public static ArrayList<User> getUsers(JDA jda, String text, Format... formats) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        ArrayList<User> result = new ArrayList<>();
        int rawFormmats = Format.getValues(formats);
        if (Format.MENTION.isPresent(rawFormmats)) {
            result.addAll(getMentionedUsers(jda, text));
            if (!result.isEmpty())
                return result;
        }
        if (Format.NAME.isPresent(rawFormmats)) {
            result.addAll(jda.getUsersByName(text, true));
            if (!result.isEmpty())
                return result;
        }
        if (Format.ID.isPresent(rawFormmats)) {
            try {
                result.add(jda.getUserById(text));
            } catch (Exception ignore) {}
        }

        //TODO getByNameDiscriminator
        return result;
    }

    public static User getUser(JDA jda, String text, Format... formats) {
        ArrayList<User> result = getUsers(jda, text, formats);
        if (result.isEmpty())
            return null;
        return result.get(0);
    }

    public static ArrayList<Member> getMentionedMembers(Guild guild, String text) {
        if (guild == null)
            throw new IllegalArgumentException("The guild cannot be null");
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        HashSet<Long> foundIds = new HashSet<>();
        ArrayList<Member> members = new ArrayList<>();
        Matcher matcher = Message.MentionType.USER.getPattern().matcher(text);
        long id;
        Member member;
        while (matcher.find()) {
            try {
                id = parseSnowflake(matcher.group(1));
                if (foundIds.contains(id))
                    continue;
                foundIds.add(id);
                member = guild.getMemberById(id);
                if (member != null)
                    members.add(member);
            } catch (NumberFormatException ignored) {}
        }
        return members;
    }

    public static ArrayList<Member> getMembers(Guild guild, String text, Format... formats) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        ArrayList<Member> result = new ArrayList<>();
        int rawFormmats = Format.getValues(formats);
        if (Format.MENTION.isPresent(rawFormmats)) {
            result.addAll(getMentionedMembers(guild, text));
            if (!result.isEmpty())
                return result;
        }
        if (Format.NAME.isPresent(rawFormmats)) {
            result.addAll(guild.getMembersByName(text, true));
            if (!result.isEmpty())
                return result;
        }
        if (Format.ID.isPresent(rawFormmats)) {
            try {
                result.add(guild.getMemberById(text));
                if (!result.isEmpty())
                    return result;
            } catch (Exception ignore) {}
        }
        if (Format.NICKNAME.isPresent(rawFormmats)) {
            result.addAll(guild.getMembersByNickname(text, true));
            if (!result.isEmpty())
                return result;
        }
        if (Format.EFFECTIVE_NAME.isPresent(rawFormmats))
            result.addAll(guild.getMembersByEffectiveName(text, true));
        //TODO getByNameDiscriminator
        return result;
    }

    public static Member getMember(Guild guild, String text, Format... formats) {
        ArrayList<Member> result = getMembers(guild, text, formats);
        if (result.isEmpty())
            return null;
        return result.get(0);
    }

    public static ArrayList<Role> getMentionedRoles(JDA jda, Guild guild, String text) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        HashSet<Long> foundIds = new HashSet<>();
        ArrayList<Role> roles = new ArrayList<>();
        Matcher matcher = ROLE_PATTERN.matcher(text);
        long id;
        Role role;
        while (matcher.find()) {
            try {
                id = parseSnowflake(matcher.group(1));
                if (foundIds.contains(id))
                    continue;
                foundIds.add(id);
                role = null;
                if (guild != null)
                    role = guild.getRoleById(id);
                if (role == null)
                    role = jda.getRoleById(id);
                if (role != null)
                    roles.add(role);
            } catch (NumberFormatException ignored) {}
        }
        return roles;
    }

    public static ArrayList<Role> getRoles(Guild guild, String text, Format... formats) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        ArrayList<Role> result = new ArrayList<>();
        int rawFormats = Format.getValues(formats);
        if (Format.MENTION.isPresent(rawFormats)) {
            result.addAll(getMentionedRoles(guild.getJDA(), guild, text));
            if (!result.isEmpty())
                return result;
        }
        if (Format.NAME.isPresent(rawFormats)) {
            result.addAll(guild.getRolesByName(text, true));
            if (!result.isEmpty())
                return result;
        }
        if (Format.ID.isPresent(rawFormats)) {
            try {
                result.add(guild.getRoleById(text));
            } catch (Exception ignore) {}
        }
        return result;
    }

    public static Role getRole(Guild guild, String text, Format... formats) {
        ArrayList<Role> result = getRoles(guild, text, formats);
        if (result.isEmpty())
            return null;
        return result.get(0);
    }

    public static ArrayList<CustomEmoji> getMentionedEmojis(JDA jda, String text) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        HashSet<Long> foundIds = new HashSet<>();
        ArrayList<CustomEmoji> emojis = new ArrayList<>();
        Matcher matcher = Message.MentionType.EMOJI.getPattern().matcher(text);
        long id;
        CustomEmoji emoji;
        String emojiName;
        boolean animated;
        while (matcher.find()) {
            try {
                id = parseSnowflake(matcher.group(2));
                if (foundIds.contains(id))
                    continue;
                foundIds.add(id);
                emojiName = matcher.group(1);
                animated = matcher.group(0).startsWith("<a:");
                emoji = jda.getEmojiById(id);
                if (emoji == null)
                    emoji = Emoji.fromCustom(emojiName, id, animated);
                emojis.add(emoji);
            }
            catch (NumberFormatException ignored) {}
        }
        return emojis;
    }

    public static ArrayList<Emoji> getEmojis(JDA jda, String text, Format... formats) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        ArrayList<Emoji> result = new ArrayList<>();
        int rawFormats = Format.getValues(formats);
        if (Format.MENTION.isPresent(rawFormats)) {
            result.addAll(getMentionedEmojis(jda, text));
            if (!result.isEmpty())
                return result;
        }
        if (Format.NAME.isPresent(rawFormats)) {
            result.addAll(jda.getEmojisByName(text, true));
            if (!result.isEmpty())
                return result;
        }
        if (Format.ID.isPresent(rawFormats)) {
            try {
                result.add(jda.getEmojiById(text));
            } catch (Exception ignore) {}
        }
        return result;
    }

    public static Emoji getEmoji(JDA jda, String text, Format... formats) {
        ArrayList<Emoji> result = getEmojis(jda, text, formats);
        if (result.isEmpty())
            return null;
        return result.get(0);
    }

    public static ArrayList<Emoji> getGuildMentionedEmojis(Guild guild, String text) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        HashSet<Long> foundIds = new HashSet<>();
        ArrayList<Emoji> emojis = new ArrayList<>();
        Matcher matcher = Message.MentionType.EMOJI.getPattern().matcher(text);
        long id;
        Emoji emoji;
        String emojiName;
        boolean animated;
        while (matcher.find()) {
            try {
                id = parseSnowflake(matcher.group(2));
                if (foundIds.contains(id))
                    continue;
                foundIds.add(id);
                emojiName = matcher.group(1);
                animated = matcher.group(0).startsWith("<a:");
                emoji = guild.getEmojiById(id);
                if (emoji == null)
                    emoji = Emoji.fromCustom(emojiName, id, animated);
                emojis.add(emoji);
            }
            catch (NumberFormatException ignored) {}
        }
        return emojis;
    }

    public static ArrayList<Emoji> getGuildEmojis(Guild guild, String text, Format... formats) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        ArrayList<Emoji> result = new ArrayList<>();
        int rawFormats = Format.getValues(formats);
        if (Format.MENTION.isPresent(rawFormats)) {
            result.addAll(getGuildMentionedEmojis(guild, text));
            if (!result.isEmpty())
                return result;
        }
        if (Format.NAME.isPresent(rawFormats)) {
            result.addAll(guild.getEmojisByName(text, true));
            if (!result.isEmpty())
                return result;
        }
        if (Format.ID.isPresent(rawFormats)) {
            try {
                result.add(guild.getEmojiById(text));
            } catch (Exception ignore) {}
        }
        return result;
    }

    public static Emoji getGuildEmoji(Guild guild, String text, Format... formats) {
        ArrayList<Emoji> result = getGuildEmojis(guild, text, formats);
        if (result.isEmpty())
            return null;
        return result.get(0);
    }

    public static ArrayList<TextChannel> getMentionedChannels(JDA jda, String text) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        HashSet<Long> foundIds = new HashSet<>();
        ArrayList<TextChannel> channelMentions = new ArrayList<>();
        Matcher matcher = Message.MentionType.CHANNEL.getPattern().matcher(text);
        long id;
        TextChannel channel;
        while (matcher.find()) {
            try {
                id = parseSnowflake(matcher.group(1));
                if (foundIds.contains(id))
                    continue;
                foundIds.add(id);
                channel = jda.getTextChannelById(id);
                if (channel != null)
                    channelMentions.add(channel);
            }
            catch (NumberFormatException ignored) {}
        }
        return channelMentions;
    }

    public static ArrayList<TextChannel> getChannels(JDA jda, String text, Format... formats) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        ArrayList<TextChannel> result = new ArrayList<>();
        int rawFormats = Format.getValues(formats);
        if (Format.MENTION.isPresent(rawFormats)) {
            result.addAll(getMentionedChannels(jda, text));
            if (!result.isEmpty())
                return result;
        }
        if (Format.NAME.isPresent(rawFormats)) {
            result.addAll(jda.getTextChannelsByName(text, true));
            if (!result.isEmpty())
                return result;
        }
        if (Format.ID.isPresent(rawFormats)) {
            try {
                result.add(jda.getTextChannelById(text));
            } catch (Exception ignore) {}
        }
        return result;
    }

    public static ArrayList<TextChannel> getGuildMentionedChannels(Guild guild, String text) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        HashSet<Long> foundIds = new HashSet<>();
        ArrayList<TextChannel> channelMentions = new ArrayList<>();
        Matcher matcher = Message.MentionType.CHANNEL.getPattern().matcher(text);
        long id;
        TextChannel channel;
        while (matcher.find()) {
            try {
                id = parseSnowflake(matcher.group(1));
                if (foundIds.contains(id))
                    continue;
                foundIds.add(id);
                channel = guild.getTextChannelById(id);
                if (channel != null)
                    channelMentions.add(channel);
            }
            catch (NumberFormatException ignored) {}
        }
        return channelMentions;
    }

    public static ArrayList<TextChannel> getGuildChannels(Guild guild, String text, Format... formats) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        ArrayList<TextChannel> result = new ArrayList<>();
        int rawFormats = Format.getValues(formats);
        if (Format.MENTION.isPresent(rawFormats)) {
            result.addAll(getGuildMentionedChannels(guild, text));
            if (!result.isEmpty())
                return result;
        }
        if (Format.NAME.isPresent(rawFormats)) {
            result.addAll(guild.getTextChannelsByName(text, true));
            if (!result.isEmpty())
                return result;
        }
        if (Format.ID.isPresent(rawFormats)) {
            try {
                result.add(guild.getTextChannelById(text));
            } catch (Exception ignore) {}
        }
        return result;
    }

    public static ArrayList<IMentionable> getMentions(JDA jda, Guild guild, String text,
                                                      Message.MentionType... types) {
        if (text == null || text.isEmpty())
            return new ArrayList<>();
        if (types == null || types.length == 0)
            return getMentions(jda, guild, text, Message.MentionType.values());
        ArrayList<IMentionable> mentions = new ArrayList<>();
        boolean channel = false;
        boolean role = false;
        boolean user = false;
        boolean emoji = false;
        for (Message.MentionType type : types) {
            switch (type) {
                case EVERYONE:
                case HERE:
                default: continue;
                case CHANNEL:
                    if (!channel)
                        mentions.addAll(getMentionedChannels(jda, text));
                    channel = true;
                    break;
                case USER:
                    if (!user)
                        mentions.addAll(getMentionedUsers(jda, text));
                    user = true;
                    break;
                case ROLE:
                    if (!role)
                        mentions.addAll(getMentionedRoles(jda, guild, text));
                    role = true;
                    break;
                case EMOJI:
                    if (!emoji)
                        mentions.addAll(getMentionedEmojis(jda, text));
                    emoji = true;
            }
        }
        return mentions;
    }
}
