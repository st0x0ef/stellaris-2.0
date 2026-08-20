package org.exodusstudio.stellaris.client.utils.stellardown;

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

            if (input.charAt(i) == '\n') {

                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                tokens.add(new Token(TokenType.NEWLINE, null));

                i++;
                textStart = i;
            }
            else if (input.startsWith("**", i)) {
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
            else if (input.startsWith("[color=", i) && input.indexOf(']', i) != -1) {
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

            } else if (input.startsWith("[ref=", i) && input.indexOf(']', i) != -1) {
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
            } else if (input.startsWith("[image=", i) && input.indexOf(']', i) != -1) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                int end = input.indexOf(']', i);

                String value = input.substring(i + "[image=".length(), end);
                tokens.add(new Token(TokenType.IMAGE, value));

                i = end + 1;
                textStart = i;
            } else if (input.startsWith("[entity=", i) && input.indexOf(']', i) != -1) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                int end = input.indexOf(']', i);

                String value = input.substring(i + "[entity=".length(), end);
                tokens.add(new Token(TokenType.ENTITY, value));

                i = end + 1;
                textStart = i;
            }
            else if (input.startsWith("[item=", i) && input.indexOf(']', i) != -1) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                int end = input.indexOf(']', i);

                String value = input.substring(i + "[item=".length(), end);
                tokens.add(new Token(TokenType.ITEM, value));

                i = end + 1;
                textStart = i;
            } else if (input.startsWith("[tl=", i) && input.indexOf(']', i) != -1) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                int end = input.indexOf(']', i);

                String value = input.substring(i + "[tl=".length(), end); // extract the color
                tokens.add(new Token(TokenType.TOOLTIP_OPEN, value));
                i = end + 1;
                textStart = i;

            } else if (input.startsWith("[tl]", i)) {
                if (i > textStart)
                    tokens.add(new Token(TokenType.TEXT, input.substring(textStart, i)));

                tokens.add(new Token(TokenType.TOOLTIP_CLOSE, null));
                i += "[tl]".length();
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
                case NEWLINE:
                    segments.add(new Pair<>("\n", Style.DEFAULT));
                    break;
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

                case TOOLTIP_OPEN:
                    styleStack.push(styleStack.peek().withTooltip(token.content()));
                    break;
                case TOOLTIP_CLOSE:
                    if (styleStack.size() > 1) styleStack.pop();
                    break;

                case REF_OPEN:
                    styleStack.push(styleStack.peek().withRef(token.content()));
                    break;
                case REF_CLOSE:
                    if (styleStack.size() > 1) styleStack.pop();
                    break;
                case IMAGE:
                    segments.add(new Pair<>("", styleStack.peek().withImage(StellardownStyle.ImageStyle.parse(token.content()))));
                    break;
                case ENTITY:

                    segments.add(new Pair<>("", styleStack.peek().withEntity(StellardownStyle.EntityStyle.parse(token.content()))));
                    break;
                case ITEM:
                    segments.add(new Pair<>("", styleStack.peek().withItem(StellardownStyle.ItemStyle.parse(token.content()))));
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

        public boolean bold ;
        public String color ;
        public boolean italic ;
        public boolean underline ;
        public String ref;
        public boolean strikethrough;
        public boolean obfuscated;
        public boolean translatable;
        public StellardownStyle.ImageStyle image;
        public StellardownStyle.EntityStyle entityStyle;
        public StellardownStyle.ItemStyle itemStyle;
        public String tooltip;
        public static final Style DEFAULT = new Style();


        private Style(boolean bold, boolean italic, boolean underline, String color, String ref, boolean strikethrough, boolean obfuscated, boolean translatable, StellardownStyle.ImageStyle image, StellardownStyle.EntityStyle entityStyle,  StellardownStyle.ItemStyle itemStyle, String tooltip) {
            this.bold = bold;
            this.italic = italic;
            this.underline = underline;
            this.color = color;
            this.ref = ref;
            this.strikethrough = strikethrough;
            this.obfuscated = obfuscated;
            this.translatable = translatable;
            this.image = image;
            this.entityStyle = entityStyle;
            this.itemStyle = itemStyle;
            this.tooltip = tooltip;
        }

        public Style() {
            this(false, false, false, "white", null, false, false, false, null, null, null, null);
        }

        public Style withBold(boolean bold) {
            return new Style(bold, this.italic, this.underline, this.color, this.ref, this.strikethrough, this.obfuscated, this.translatable, this.image, this.entityStyle, this.itemStyle, this.tooltip);
        }

        public Style withStrikethrough(boolean strikethrough) {
            return new Style(this.bold, this.italic, this.underline, this.color, this.ref, strikethrough, this.obfuscated, this.translatable, this.image, this.entityStyle, this.itemStyle, this.tooltip);
        }

        public Style withItalic(boolean italic) {
            return new Style(this.bold, italic, this.underline, this.color, this.ref, this.strikethrough, this.obfuscated, this.translatable, this.image, this.entityStyle, this.itemStyle, this.tooltip);
        }

        public Style withObfuscated(boolean obfuscated) {
            return new Style(this.bold, this.italic, this.underline, this.color, this.ref, this.strikethrough, obfuscated, this.translatable, this.image, this.entityStyle, this.itemStyle, this.tooltip);
        }

        public Style withUnderline(boolean underline) {
            return new Style(this.bold, this.italic, underline, this.color, this.ref, this.strikethrough, this.obfuscated, this.translatable, this.image, this.entityStyle, this.itemStyle, this.tooltip);
        }

        public Style withColor(String color) {
            return new Style(this.bold, this.italic, this.underline, color, this.ref, this.strikethrough, this.obfuscated, this.translatable, this.image, this.entityStyle, this.itemStyle, this.tooltip);
        }

        public Style withRef(String ref) {
            return new Style(this.bold, this.italic, this.underline, this.color, ref, this.strikethrough, this.obfuscated, this.translatable, this.image, this.entityStyle, this.itemStyle, this.tooltip);
        }

        public Style withTranslatable(boolean translatable) {
            return new Style(this.bold, this.italic, this.underline, this.color, this.ref, this.strikethrough, this.obfuscated, translatable, this.image, this.entityStyle, this.itemStyle, this.tooltip);
        }

        public Style withImage(StellardownStyle.ImageStyle image) {
            return new Style(this.bold, this.italic, this.underline, this.color, this.ref, this.strikethrough, this.obfuscated, this.translatable, image, this.entityStyle, this.itemStyle, this.tooltip);
        }

        public Style withEntity(StellardownStyle.EntityStyle entityStyle) {
            return new Style(this.bold, this.italic, this.underline, this.color, this.ref, this.strikethrough, this.obfuscated, this.translatable, this.image, entityStyle, this.itemStyle, this.tooltip);
        }

        public Style withItem(StellardownStyle.ItemStyle itemStyle) {
            return new Style(this.bold, this.italic, this.underline, this.color, this.ref, this.strikethrough, this.obfuscated, this.translatable, this.image, this.entityStyle, itemStyle, this.tooltip);
        }

        public Style withTooltip(String tooltip) {
            return new Style(this.bold, this.italic, this.underline, this.color, this.ref, this.strikethrough, this.obfuscated, this.translatable, this.image, this.entityStyle, this.itemStyle, tooltip);
        }
    }

    public enum TokenType {
        BOLD, ITALIC, STRIKETHROUGH, UNDERLINE, OBFUSCATED, COLOR_OPEN, COLOR_CLOSE, REF_OPEN, REF_CLOSE, NEWLINE, TEXT, TRANSLATABLE_OPEN, TRANSLATABLE_CLOSE, IMAGE, ENTITY, ITEM, TOOLTIP_OPEN, TOOLTIP_CLOSE

    }

}
