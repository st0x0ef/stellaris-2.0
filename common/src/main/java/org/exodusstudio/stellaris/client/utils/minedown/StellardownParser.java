package org.exodusstudio.stellaris.client.utils.minedown;

import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * A simple parser that transforms a text into tokens and then into styled segments. It supports the following syntax:
 * - **bold** for bold text
 * - *italic* for italic text
 * - __underline__ for underline text
 * - ~~strikethrough~~ for strikethrough text
 * - $$obfuscated$$ for obfuscated text
 * - [color=color]text[/color] for colored text
 * - [ref=location]text[/ref] for reference text (used for wiki)
 */
public class StellardownParser {

    public List<Token> tokenize(String input) {
        ArrayList<Token> tokens = new ArrayList<>();
        int i = 0;
        int textStart = 0;

        while (i < input.length()) {
            if (input.startsWith("**", i)) {
                // Flush any accumulated plain text first
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                tokens.add(new Token(TokenType.BOLD, "**"));
                i += 2;
                textStart = i;

            } else if (input.startsWith("*", i)) {
                // Flush any accumulated plain text first
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                tokens.add(new Token(TokenType.ITALIC, "*"));
                i += 1;
                textStart = i;

            }  else if (input.startsWith("__", i)) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                tokens.add(new Token(TokenType.UNDERLINE, "__"));
                i += 2;
                textStart = i;

            }
            else if (input.startsWith("$$", i)) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                tokens.add(new Token(TokenType.OBFUSCATED, "$$"));
                i += 2;
                textStart = i;

            }
            else if (input.startsWith("~~", i)) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                tokens.add(new Token(TokenType.STRIKETHROUGH, "~~"));
                i += 2;
                textStart = i;
            }
            else if (input.startsWith("[tr]", i)) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                tokens.add(new Token(TokenType.TRANSLATABLE_OPEN, "[tr]"));
                i += 4;
                textStart = i;
            }
            else if (input.startsWith("[/tr]", i)) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                tokens.add(new Token(TokenType.TRANSLATABLE_CLOSE, "[/tr]"));
                i += 5;
                textStart = i;
            }
            else if (input.startsWith("[color=", i)) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                int end = input.indexOf(']', i);
                String value = input.substring(i + 7, end); // extract the color
                tokens.add(new Token(TokenType.COLOR_OPEN, value));
                i = end + 1;
                textStart = i;

            } else if (input.startsWith("[color]", i)) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                tokens.add(new Token(TokenType.COLOR_CLOSE, null));
                i += 7;
                textStart = i;

            } else if (input.startsWith("[ref=", i)) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                int end = input.indexOf(']', i);
                String value = input.substring(i + 5, end);
                tokens.add(new Token(TokenType.REF_OPEN, value));
                i = end + 1;
                textStart = i;

            } else if (input.startsWith("[ref]", i)) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                tokens.add(new Token(TokenType.REF_CLOSE, null));
                i += 5;
                textStart = i;
            }

            else {
                i++;
            }
        }

        // Don't forget trailing text
        if (textStart < input.length())
            tokens.add(new Token(TokenType.TEXT, input.substring(textStart)));

        return tokens;


    }

    public List<Pair<String, Style>> parse(List<Token> tokens) {
        List<Pair<String, Style>> segments = new ArrayList<>();
        Deque<Style> styleStack = new ArrayDeque<>();
        styleStack.push(Style.DEFAULT);

        // Track toggle state for symmetric delimiters like **
        boolean boldOpen = false;
        boolean italicOpen = false;
        boolean underlineOpen = false;
        boolean strikeThroughtOpen = false;
        boolean obfuscatedOpen = false;

        for (Token token : tokens) {
            switch (token.tokenType) {
                case BOLD:
                    if (!boldOpen) {
                        styleStack.push(styleStack.peek().withBold(true));
                        boldOpen = true;
                    } else {
                        styleStack.pop();
                        boldOpen = false;
                    }
                    break;
                case COLOR_OPEN:
                    styleStack.push(styleStack.peek().withColor(token.content()));
                    break;

                case COLOR_CLOSE:
                    if (styleStack.size() > 1) styleStack.pop();
                    break;
                case ITALIC:
                    if (!italicOpen) {
                        styleStack.push(styleStack.peek().withItalic(true));
                        italicOpen = true;
                    } else {
                        styleStack.pop();
                        italicOpen = false;
                    }
                    break;
                case UNDERLINE:
                    if (!underlineOpen) {
                        styleStack.push(styleStack.peek().withUnderline(true));
                        underlineOpen = true;
                    } else {
                        styleStack.pop();
                        underlineOpen = false;
                    }
                    break;
                case STRIKETHROUGH:
                    if (!strikeThroughtOpen) {
                        styleStack.push(styleStack.peek().withStrikethrough(true));
                        strikeThroughtOpen = true;
                    } else {
                        styleStack.pop();
                        strikeThroughtOpen = false;
                    }
                    break;
                case OBFUSCATED:
                    if (!obfuscatedOpen) {
                        styleStack.push(styleStack.peek().withObfuscated(true));
                        obfuscatedOpen = true;
                    } else {
                        styleStack.pop();
                        obfuscatedOpen = false;
                    }
                    break;

                case TRANSLATABLE_OPEN:
                    styleStack.push(styleStack.peek().withTranslatable(true));
                    break;

                case TRANSLATABLE_CLOSE:
                    if (styleStack.size() > 1) styleStack.pop();
                    break;
                case REF_OPEN:
                    styleStack.push(styleStack.peek().withRef(token.content()));
                    break;

                case REF_CLOSE:
                    if (styleStack.size() > 1) styleStack.pop();
                    break;
                case TEXT:
                    segments.add(new Pair<>(token.content(), styleStack.peek()));
                    break;
            }
        }

        return segments;
    }

    public record Token(TokenType tokenType, String content) {
        @Override
        public @NotNull String toString() {
            return tokenType + (content != null ? " ('" + content + "')" : "");
        }
    }

    public static class Style {

        boolean bold ;
        String color ;
        boolean italic ;
        boolean underline ;
        String ref;
        boolean strikethrough;
        boolean obfuscated;
        boolean translatable;
        public static final Style DEFAULT = new Style();

        private Style(boolean bold, boolean italic, boolean underline, String color, String ref, boolean strikethrough, boolean obfuscated, boolean translatable) {
            this.bold = bold;
            this.italic = italic;
            this.underline = underline;
            this.color = color;
            this.ref = ref;
            this.strikethrough = strikethrough;
            this.obfuscated = obfuscated;
            this.translatable = translatable;
        }

        public Style() {
            this(false, false, false, "white", null, false, false, false);
        }

        public Style withBold(boolean bold) {
            return new Style(bold, this.italic, this.underline, this.color, this.ref, this.strikethrough, this.obfuscated, this.translatable);
        }

        public Style withStrikethrough(boolean strikethrough) {
            return new Style(this.bold, this.italic, this.underline, this.color, this.ref, strikethrough, this.obfuscated, this.translatable);
        }

        public Style withItalic(boolean italic) {
            return new Style(this.bold, italic, this.underline, this.color, this.ref, this.strikethrough, this.obfuscated, this.translatable);
        }

        public Style withObfuscated(boolean obfuscated) {
            return new Style(this.bold, this.italic, this.underline, this.color, this.ref, this.strikethrough, obfuscated, this.translatable);
        }

        public Style withUnderline(boolean underline) {
            return new Style(this.bold, this.italic, underline, this.color, this.ref, this.strikethrough, this.obfuscated, this.translatable);
        }

        public Style withColor(String color) {
            return new Style(this.bold, this.italic, this.underline, color, this.ref, this.strikethrough, this.obfuscated, this.translatable);
        }

        public Style withRef(String ref) {
            return new Style(this.bold, this.italic, this.underline, this.color, ref, this.strikethrough, this.obfuscated, this.translatable);
        }

        public Style withTranslatable(boolean translatable) {
            return new Style(this.bold, this.italic, this.underline, this.color, this.ref, this.strikethrough, this.obfuscated, translatable);
        }
    }

    public enum TokenType {
        BOLD, ITALIC, STRIKETHROUGH, UNDERLINE, OBFUSCATED, COLOR_OPEN, COLOR_CLOSE, REF_OPEN, REF_CLOSE, NEWLINE, TEXT, TRANSLATABLE_OPEN, TRANSLATABLE_CLOSE
    }
}
