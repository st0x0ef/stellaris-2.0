package org.exodusstudio.stellaris.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.exodusstudio.stellaris.client.screen.components.WikiEntryWidget;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WikiEntryTextRenderer {

    public final GuiGraphics guiGraphics;
    public final String text;
    public final int maxWidth;


    public String color = null;
    public String resourcelocation = null;

    public ArrayList<ArrayList<Word>> lines = new ArrayList<>();

    public WikiEntryTextRenderer(GuiGraphics guiGraphics, String text, int maxWidth) {
        this.guiGraphics = guiGraphics;
        this.text = text;
        this.maxWidth = maxWidth;
        this.lines = createLines(text, maxWidth);
    }

    public ArrayList<ArrayList<Word>> createLines(String message, int maxWidth) {

        String[] words = message.split("\\s+");

        ArrayList<ArrayList<Word>> lines = new ArrayList<>();

        ArrayList<Word> wordsInLine = new ArrayList<>();

        AtomicInteger remainingWords = new AtomicInteger(words.length);
        AtomicInteger width = new AtomicInteger(words.length);

        for (String word : words) {
            remainingWords.getAndDecrement();

            int wordWidth = Minecraft.getInstance().font.width(word + " ");

            if (word.contains("[br]")) {
                lines.add(wordsInLine);
                wordsInLine = new ArrayList<>();
                width.set(0);
            } else {

                if (word.contains("[color=")) {
                    String wordWithoutColor = word.replace("[color=" + word.substring(7, word.indexOf("]")) + "]", "");
                    this.color = word.substring(7, word.indexOf("]"));
                    wordWidth = Minecraft.getInstance().font.width(wordWithoutColor + " ");
                }
                else if (word.contains("[ref=")) {
                    this.color = word.substring(7, word.indexOf("]"));
                    wordWidth = Minecraft.getInstance().font.width(removeRef(word) + " ");
                }


                if(word.equals("[color]")) {
                    this.color = null;
                    break;
                } else if (word.equals("[ref]")) {
                    this.resourcelocation = null;
                    break;
                }

                Word wordObj = new Word(word);

                if(this.color != null) {
                    wordObj.color = this.color;
                }
                if(this.resourcelocation != null) {
                    wordObj.resourceLocation = this.resourcelocation;
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

    public int renderWords(int x, int y, Consumer<WikiEntryWidget.ClickBox> clickBoxConsumer) {
        for (int i = 0; i < lines.size(); i++) {
            ArrayList<Word> words = lines.get(i);
            AtomicInteger width = new AtomicInteger(0);
            for (Word word : words) {

                String color = "white";

                if (word.color != null) {
                    color = word.color;
                }

                if (word.resourceLocation != null) {
                    clickBoxConsumer.accept(new WikiEntryWidget.ClickBox(x + width.get(), y + (i * getFont().lineHeight), getFont().width(word.text), getFont().lineHeight, word.resourceLocation));
                    color = "blue";
                }

                this.guiGraphics.drawString(getFont(), word.text, x + width.get(), y + (i * getFont().lineHeight), Utils.getColorHexCode(color));
                width.addAndGet(Minecraft.getInstance().font.width(word + " "));
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
            return (color != null ? " [color=" + color + "]" : "") + (resourceLocation != null ? " [ref=" + resourceLocation + "]" : "") + text;
        }
    }

}
