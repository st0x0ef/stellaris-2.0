package org.exodusstudio.stellaris.client.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A Class to parse and render our "custom" markdown system.
 */
public class WikiEntryTextRenderer {

    public final String text;
    public final int maxWidth;


    public String color = null;
    public String referenceLocation = null;
    public String tooltip = null;
    public boolean bold = false;
    public boolean italic = false;
    public boolean underline = false;

    public ArrayList<ArrayList<Word>> lines = new ArrayList<>();

    public WikiEntryTextRenderer(String text, int maxWidth) {
        this.text = text;
        this.maxWidth = maxWidth;
        this.lines = parseLines(text, maxWidth);
    }

    /**
     * Return a list of words in a lines.
     * @param message The message we want to render
     * @param maxWidth The width of the place we want our text to be rendered
     * @return A list of line containing a list of words.
     */
    public ArrayList<ArrayList<Word>> parseLines(String message, int maxWidth) {

        String[] words = message.split("\\s+");

        //This will hold all the lines of words
        ArrayList<ArrayList<Word>> lines = new ArrayList<>();

        //This holds the current line of words
        ArrayList<Word> wordsInLine = new ArrayList<>();

        AtomicInteger remainingWords = new AtomicInteger(words.length);
        //AtomicInteger width = new AtomicInteger(words.length);
        AtomicInteger width = new AtomicInteger(0);

        for (String word : words) {
            remainingWords.getAndDecrement();

            int wordWidth = Minecraft.getInstance().font.width(word + " ");

            // Handle line breaks
            if (word.contains("[br]")) {

                //remove the tag and check if there's any text left, if not just break the line
                word = word.replace("[br]", "");
                if(!word.isEmpty()) {
                    Word wordObj = createWord(word);

                    wordsInLine.add(wordObj);
                }

                lines.add(wordsInLine);
                wordsInLine = new ArrayList<>();
                width.set(0);
            } else {

                // Handle opening tags
                if (word.contains("[color=")) {
                    this.color = word.substring(7, word.indexOf("]"));
                    word = word.replace("[color=" + word.substring(7, word.indexOf("]")) + "]", "");

                    if(word.isEmpty()) continue;


                    wordWidth = Minecraft.getInstance().font.width(word + " ");
                }
                if (word.contains("[ref=")) {
                    this.referenceLocation = word.substring(5, word.indexOf("]"));
                    word = removeTag(word, "ref");

                    //If it's only the tag, skip it
                    if(word.isEmpty()) continue;

                    wordWidth = Minecraft.getInstance().font.width(word + " ");
                }
                if (word.contains("[tl=")) {
                    this.tooltip = word.substring(4, word.indexOf("]"));
                    word = removeTag(word, "tl");

                    //If it's only the tag, skip it
                    if(word.isEmpty()) continue;

                    wordWidth = Minecraft.getInstance().font.width(word + " ");
                }
                if (word.contains("**")) {
                    word = word.replace("**", "");
                    this.bold = !bold;
                }
                if (word.contains("__")) {
                    word = word.replace("__", "");
                    this.underline = !underline;
                }
                if (word.contains("*")) {
                    word = word.replace("*", "");
                    this.italic = !italic;
                }


                // Handle closing tags
                if(word.contains("[color]")) {
                    this.color = null;
                    word = word.replace("[color]", "");
                } else if (word.contains("[ref]")) {
                    this.referenceLocation = null;
                    word = word.replace("[ref]", "");
                } else if (word.contains("[tl]")) {
                    this.tooltip = null;
                    word = word.replace("[tl]", "");
                }

                // Create a new Word object for the current word
                Word wordObj = createWord(word);

                if (wordWidth + width.get() < maxWidth) {
                    if (remainingWords.get() == 0) {
                        wordsInLine.add(wordObj);
                        lines.add(wordsInLine);
                        break;
                    }

                    wordsInLine.add(wordObj);
                    width.addAndGet(wordWidth);
                }
                else {
                    width.set(0);
                    lines.add(wordsInLine);
                    wordsInLine = new ArrayList<>();
                    wordsInLine.add(wordObj);
                }
            }
        }
        return lines;
    }

    /**
     * Create a Word object from a string, applying the current color and reference location if they are set.
     * @param word The text of the word to create.
     * @return A Word object with the appropriate color and reference location applied.
     */
    public Word createWord(String word) {
        Word wordObj = new Word(word);

        //Add color and reference location if they are set
        if(this.color != null) {
            wordObj.color = this.color;
        }
        if(this.referenceLocation != null) {
            wordObj.identifier = this.referenceLocation;
        }
        if(this.tooltip != null) {
            wordObj.tooltip = this.tooltip;
        }
        if(this.bold) {
            wordObj.bold = this.bold;
        }
        if(this.underline) {
            wordObj.underline = this.underline;
        }
        if(this.italic) {
            wordObj.italic = this.italic;
        }

        return wordObj;
    }


    public int renderWords(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, Consumer<ActionBox> clickBoxConsumer) {
        for (int i = 0; i < lines.size(); i++) {
            ArrayList<Word> words = lines.get(i);

            int width = 0;
            for (Word word : words) {
                String color = "white";

                if (word.color != null) {
                    color = word.color;
                }

                if (word.identifier != null) {
                    clickBoxConsumer.accept(new ActionBox(x + width, y + (i * getFont().lineHeight), getFont().width(word.text), getFont().lineHeight, null, (info) -> info.actionBox().changePage(info.infoWidget(), word.identifier), (word.text + word.identifier)));
                    color = "blue";
                }
                if (word.tooltip != null) {
                    //TODO add tooltip support.
                    clickBoxConsumer.accept(new ActionBox(x + width, y + (i * getFont().lineHeight), getFont().width(word.text), getFont().lineHeight, (info) -> {
                        //info.infoWidget().setTooltip(Tooltip.create(Component.literal("eee")));
                    }, null, (word.text + word.tooltip)));
                    color = "green";
                }

                guiGraphics.text(getFont(), word.getText(), x + width, y + (i * getFont().lineHeight), Utils.getMinecraftColor(color));
                width += getFont().width(word.text + " ");
            }
            width = 0;
        }
        return lines.size() * getFont().lineHeight;
    }

    public Font getFont() {
        return Minecraft.getInstance().font;
    }

    public String removeTag(String text, String tag) {
        String regex = "\\[" + tag + "=.*?\\]";
        return text.replaceAll(regex, "");
    }

    public static class Word {
        public String text;
        public String color = null;
        public String identifier = null;
        public String tooltip = null;
        public boolean bold = false;
        public boolean underline = false;
        public boolean italic = false;


        public Word(String word) {
            this.text = word;
        }

        @Override
        public String toString() {
            return (!this.onlyText() ? "{" : "") + (color != null ? "[color=" + color + "]" : "") + (tooltip != null ? "[tl=" + tooltip + "]" : "") + (identifier != null ? " [ref=" + identifier + "]" : "") + text + (!this.onlyText() ? "}" : "");
        }

        public boolean onlyText() {
            return color == null && identifier == null && tooltip == null;
        }

        public Component getText() {

            MutableComponent component = Component.literal(this.text);

            if(this.bold) component.withStyle(ChatFormatting.BOLD);
            if(this.italic) component.withStyle(ChatFormatting.ITALIC);
            if(this.underline) component.withStyle(ChatFormatting.UNDERLINE);

            return component;
        }

    }

    /**
     * A builder class to create a WikiEntryTextRenderer object. It provides a more convenient way to create a WikiEntryTextRenderer object by allowing us to add text, colored text, line breaks, and conditionally colored text in a more fluent way.
     * TATHAN's Note : this is a bit overkill :)
     */
    public static class Builder {
        private final StringBuilder textBuilder = new StringBuilder();

        public Builder addText(String text) {
            textBuilder.append(" ").append(text);
            return this;
        }
        public Builder addText(Object text) {
            textBuilder.append(" ").append(text);
            return this;
        }

        public Builder addColoredText(String text, String color) {
            textBuilder.append(" [color=").append(color).append("] ").append(text).append(" [color]");
            return this;
        }

        public Builder breakL() {
            textBuilder.append(" [br] ");
            return this;
        }

        public Builder conditionColorText(String text, String color, boolean condition) {
            if(condition) {
                addColoredText(text, color);
            } else {
                addText(text);
            }
            return this;
        }

        public Builder conditionColorText(String text, String color, String color2, boolean condition) {
            if(condition) {
                addColoredText(text, color);
            } else {
                addColoredText(text, color2);
            }
            return this;
        }

        public String toString() {
            return textBuilder.toString();
        }

        public WikiEntryTextRenderer build(int maxWidth) {
            return new WikiEntryTextRenderer(textBuilder.toString(), maxWidth);
        }

        public Widget toWidget(int x, int y, int width, int height) {
            return new Widget(x, y, width, height, build(width));
        }
     }

     public static class Widget extends AbstractWidget {

         WikiEntryTextRenderer wikiEntryTextRenderer;

         public Widget(int x, int y, int width, int height, WikiEntryTextRenderer wikiEntryTextRenderer) {
             super(x, y, width, height, Component.empty());
             this.wikiEntryTextRenderer = wikiEntryTextRenderer;
         }

         @Override
         protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
             this.wikiEntryTextRenderer.renderWords(guiGraphics, this.getX(), this.getY(), mouseX, mouseY, (clickBox) -> {
                 //TODO add click box support
             });
         }

         @Override
         protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

         }
     }
}
