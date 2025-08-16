package org.exodusstudio.stellaris.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.exodusstudio.stellaris.client.screen.components.WikiInfos;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class WikiEntryTextRenderer {

    public final String text;
    public final int maxWidth;


    public String color = null;
    public String referenceLocation = null;

    public ArrayList<ArrayList<Word>> lines = new ArrayList<>();

    public WikiEntryTextRenderer(String text, int maxWidth) {
        this.text = text;
        this.maxWidth = maxWidth;
        this.lines = createLines(text, maxWidth);
    }

    public ArrayList<ArrayList<Word>> createLines(String message, int maxWidth) {

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
                else if (word.contains("[ref=")) {
                    this.referenceLocation = word.substring(5, word.indexOf("]"));
                    word = removeRef(word);

                    //If it's only the tag, skip it
                    if(word.isEmpty()) continue;

                    wordWidth = Minecraft.getInstance().font.width(word + " ");
                }


                // Handle closing tags
                if(word.equals("[color]")) {
                    this.color = null;
                    continue;
                } else if (word.equals("[ref]")) {
                    this.referenceLocation = null;
                    continue;
                }

                // Create a new Word object for the current word
                Word wordObj = new Word(word);

                //Add color and reference location if they are set
                if(this.color != null) {
                    wordObj.color = this.color;
                }
                if(this.referenceLocation != null) {
                    wordObj.resourceLocation = this.referenceLocation;
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

    public int renderWords(GuiGraphics guiGraphics, int x, int y, Consumer<WikiInfos.ClickBox> clickBoxConsumer) {
        for (int i = 0; i < lines.size(); i++) {
            ArrayList<Word> words = lines.get(i);
            AtomicInteger width = new AtomicInteger(0);
            for (Word word : words) {

                String color = "white";

                if (word.color != null) {
                    color = word.color;
                }

                if (word.resourceLocation != null) {
                    clickBoxConsumer.accept(new WikiInfos.ClickBox(x + width.get(), y + (i * getFont().lineHeight), getFont().width(word.text), getFont().lineHeight, word.resourceLocation));
                    color = "blue";
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

    public String removeRef(String text) {
        String regex = "\\[ref=.*?\\]";
        return text.replaceAll(regex, "");
    }
    
    public static class Word {

        public String text;
        public String color = null;
        public String resourceLocation = null;

        public Word(String word) {
            this.text = word;
        }

        @Override
        public String toString() {
            return (!this.onlyText() ? "{" : "") + (color != null ? "[color=" + color + "]" : "") + (resourceLocation != null ? " [ref=" + resourceLocation + "]" : "") + text + (!this.onlyText() ? "}" : "");
        }

        public boolean onlyText() {
            return color == null && resourceLocation == null;
        }
    }

}
