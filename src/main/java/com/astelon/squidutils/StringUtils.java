package com.astelon.squidutils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String capitalizeWord(String text) {
        if (text == null)
            throw new IllegalArgumentException("The given text cannot be null.");
        if (text.isEmpty())
            return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    public static String fixCapitalization(String text) {
        if (text == null)
            throw new IllegalArgumentException("The given text cannot be null.");
        if (text.isEmpty())
            return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
    }

    public static int countSubstrings(String origin, String substring) {
        int nr = 0;
        int next;
        int offset = 0;
        while ((next = origin.indexOf(substring, offset)) != -1) {
            nr++;
            offset = next + substring.length();
        }
        return nr;
    }

    public static ArrayList<String> simpleSplit(String text, String separator) {
        int separatorSize = separator.length();
        int offset = separatorSize;
        int next;
        ArrayList<String> commandStrings = new ArrayList<>();
        while ((next = text.indexOf(separator, offset)) != -1) {
            commandStrings.add(text.substring(offset, next));
            offset = next + separatorSize;
        }
        commandStrings.add(text.substring(offset));
        return commandStrings;
    }

    //TODO IllegalArgumentException pentru text null
    public static String firstWord(String text, int offset, char separator) { //TODO return "" pentru text gol?
        if (offset < 0 || offset >= text.length())
            throw new StringIndexOutOfBoundsException(offset);
        StringBuilder result = new StringBuilder();
        text = text.trim();
        int i;
        for (i = offset; i < text.length(); i++) {
            if (text.charAt(i) != separator)
                result.append(text.charAt(i));
            else
                return result.toString();
        }
        return result.toString();
    }

    public static String firstWord(String text) {
        return firstWord(text, 0, ' ');
    }

    public static String firstWord(String text, char separator) {
        return firstWord(text, 0, separator);
    }

    public static String firstWord(String text, int offset) {
        return firstWord(text, offset, ' ');
    }

    public static String firstWord(String text, int offset, String separator) {
        if (offset < 0 || offset >= text.length())
            throw new StringIndexOutOfBoundsException(offset);
        StringBuilder result = new StringBuilder();
        text = text.trim();
        int i;
        for (i = offset; i < text.length(); i++) {
            if (!text.startsWith(separator, i))
                result.append(text.charAt(i));
            else
                return result.toString();
        }
        return result.toString();
    }

    public static String firstWord(String text, String separator) {
        return firstWord(text, 0, separator);
    }

    public static String trimBeginning(String text) {
        if (text.isEmpty() || text.charAt(0) != ' ')
            return text;
        int index = 0;
        while (text.charAt(index) == ' ')
            index++;
        return text.substring(index);
    }

    public static String getContentStrippedWithMentions(String text) {
        String[] keys = new String[]{"*", "_", "`", "~~"};
        TreeSet<FormatToken> tokens = new TreeSet<>(Comparator.comparingInt(t -> t.start));
        int i;
        for (i = 0; i < keys.length; i++) {
            Matcher matcher = Pattern.compile(Pattern.quote(keys[i])).matcher(text);
            while (matcher.find())
                tokens.add(new FormatToken(keys[i], matcher.start()));
        }
        Deque<FormatToken> stack = new ArrayDeque<>();
        List<FormatToken> toRemove = new ArrayList<>();
        boolean inBlock = false;
        for (FormatToken token : tokens) {
            if (stack.isEmpty() || !stack.peek().format.equals(token.format) || stack.peek().start + token
                    .format.length() == token.start) {
                if (!inBlock) {
                    if (token.format.equals("`")) {
                        stack.clear();
                        inBlock = true;
                    }
                    stack.push(token);
                } else if (token.format.equals("`")) {
                    stack.push(token);
                }
            } else if (!stack.isEmpty()) {
                toRemove.add(stack.pop());
                toRemove.add(token);
                if (token.format.equals("`") && stack.isEmpty())
                    inBlock = false;
            }
        }
        toRemove.sort(Comparator.comparingInt(t -> t.start));
        StringBuilder out = new StringBuilder();
        int currIndex = 0;
        for (FormatToken formatToken : toRemove) {
            if (currIndex < formatToken.start)
                out.append(text, currIndex, formatToken.start);
            currIndex = formatToken.start + formatToken.format.length();
        }
        if (currIndex < text.length())
            out.append(text.substring(currIndex));
        return out.toString().
                replace("*", "\\*").
                replace("_", "\\_").
                replace("~", "\\~");
    }

    private static class FormatToken {
        public final String format;
        public final int start;

        public FormatToken(String format, int start) {
            this.format = format;
            this.start = start;
        }
    }
}
