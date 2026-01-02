package org.exodusstudio.stellaris.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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

    public ArrayList<ArrayList<Word>> lines = new ArrayList<>();

    public WikiEntryTextRenderer(String text, int maxWidth) {
        this.text = text;
        this.maxWidth = maxWidth;
        this.lines = parseLines(text, maxWidth);
    }

    /**
     *  Return a list of word in a line.
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
        AtomicInteger width = new AtomicInteger(words.length);

        for (String word : words) {
            remainingWords.getAndDecrement();

            int wordWidth = Minecraft.getInstance().font.width(word + " ");

            // Handle line breaks
            if (word.contains("[br]")) {
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
                Word wordObj = new Word(word);

                //Add color and reference location if they are set
                if(this.color != null) {
                    wordObj.color = this.color;
                }
                if(this.referenceLocation != null) {
                    wordObj.Identifier = this.referenceLocation;
                }
                if(this.tooltip != null) {
                    wordObj.tooltip = this.tooltip;
                }



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

    public int getTextHeight() {
        return lines.size() * getFont().lineHeight;
    }

    public int renderWords(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Consumer<ActionBox> clickBoxConsumer) {
        for (int i = 0; i < lines.size(); i++) {
            ArrayList<Word> words = lines.get(i);
            AtomicInteger width = new AtomicInteger(0);
            for (Word word : words) {
                String color = "white";

                if (word.color != null) {
                    color = word.color;
                }

                if (word.Identifier != null) {
                    clickBoxConsumer.accept(new ActionBox(x + width.get(), y + (i * getFont().lineHeight), getFont().width(word.text), getFont().lineHeight, null, (info) -> info.actionBox().changePage(info.infoWidget(), word.Identifier), (word.text + word.Identifier)));
                    color = "blue";
                }
                if (word.tooltip != null) {
                    //TODO add tooltip support.
                    clickBoxConsumer.accept(new ActionBox(x + width.get(), y + (i * getFont().lineHeight), getFont().width(word.text), getFont().lineHeight, (info) -> {
                        //info.infoWidget().setTooltip(Tooltip.create(Component.literal("eee")));
                    }, null, (word.text + word.tooltip)));
                    color = "green";
                }

                guiGraphics.drawString(getFont(), word.text, x + width.get(), y + (i * getFont().lineHeight), Utils.getMinecraftColor(color));
                width.addAndGet(Minecraft.getInstance().font.width(word.text + " "));
            }
            width.set(0);
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
        public String Identifier = null;
        public String tooltip = null;

        public Word(String word) {
            this.text = word;
        }

        @Override
        public String toString() {
            return (!this.onlyText() ? "{" : "") + (color != null ? "[color=" + color + "]" : "") + (tooltip != null ? "[tl=" + tooltip + "]" : "") + (Identifier != null ? " [ref=" + Identifier + "]" : "") + text + (!this.onlyText() ? "}" : "");
        }

        public boolean onlyText() {
            return color == null && Identifier == null && tooltip == null;
        }
    }

}
