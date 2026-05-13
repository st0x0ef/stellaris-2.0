package org.exodusstudio.stellaris.client.utils.minedown;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.utils.ActionBox;
import org.exodusstudio.stellaris.common.utils.Utils;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StellardownRenderer {

    private final Font font;
    private StellardownParser parser;
    private final List<Pair<String, StellardownParser.Style>> segments;
    private List<Line> renderedLines;

    public StellardownRenderer(String formattedText, int areaWidth, Font font) {
        this.font = font;
        this.parser = new StellardownParser();

        //We only parse one time the text
        this.segments = parser.parse(parser.tokenize(formattedText));
        updateLayout(areaWidth);
    }


    public void updateLayout(int areaWidth) {
        this.renderedLines = layout(areaWidth);
    }

    public List<Line> layout(int areaWidth) {
        List<Line> lines = new ArrayList<>();
        Line currentLine = new Line();
        int cursorX = 0;

        Pattern tokenPattern = Pattern.compile("\\s+|\\S+");

        for (Pair<String, StellardownParser.Style> segment : segments) {

            String text = segment.getA();
            Matcher matcher = tokenPattern.matcher(text);
            while (matcher.find()) {
                String actual = matcher.group();
                if (actual.isEmpty()) continue;

                int wordWidth = measureWord(actual, segment.getB());

                if (actual.equals("\n") || actual.equals("[br]")) {              // explicit line break
                    lines.add(currentLine);
                    currentLine = new Line();
                    cursorX = 0;

                } else if (cursorX + wordWidth > areaWidth && cursorX > 0) {  // wrap
                    lines.add(currentLine);
                    currentLine = new Line();
                    cursorX = 0;
                    String trimmed = actual.stripLeading();
                    int trimmedWidth = measureWord(trimmed, segment.getB());

                    currentLine.add(new PositionedSegment(trimmed, segment.getB(), cursorX), trimmedWidth);
                    cursorX += trimmedWidth;

                } else {
                    currentLine.add(new PositionedSegment(actual, segment.getB(), cursorX), wordWidth);
                    cursorX += wordWidth;
                }
            }
        }

        if (!currentLine.isEmpty()) lines.add(currentLine);

        int y = 0;
        for (Line line : lines) {
            line.y = y;
            y += line.height;
        }

        return lines;
    }

    public int render(int x, int y, GuiGraphics guiGraphics, Consumer<ActionBox> clickBoxConsumer) {
        for (Line line : this.renderedLines) {
            for (PositionedSegment seg : line.segments) {

                MutableComponent component = toComponent(seg.text, seg.style);


                if(seg.style.ref != null) {
                    //component.withStyle(s -> s.withHoverEvent(new HoverEvent.ShowText(Component.literal("entry: " + seg.style.ref))));

                    int width = measureWord(seg.text, seg.style);
                    clickBoxConsumer.accept(new ActionBox(x + seg.x, y + line.y, width, line.height, null, (info) -> info.actionBox().changePage(info.infoWidget(), seg.style.ref), seg.style.ref));
                }

                guiGraphics.drawString(font, component,
                        x + seg.x, y + line.y,  // use pre-computed line.y
                        Utils.getMinecraftColor("white"));

            }
        }

        if (renderedLines.isEmpty()) return 0;
        Line lastLine = renderedLines.getLast();
        return lastLine.y + lastLine.height;
    }

    public int render(int x, int y, GuiGraphics guiGraphics) {
        return render(x, y, guiGraphics, box -> {});
    }


    class Line {
        List<PositionedSegment> segments;
        int totalWidth;
        int height;
        int y;

        public Line() {
            this.segments = new ArrayList<>();
            this.totalWidth = 0;
            this.height = 0;
        }
        public void add(PositionedSegment segment, int width) {
            segments.add(segment);
            totalWidth = segment.x + width; // not just width
            height = Math.max(height, font.lineHeight);
        }

        boolean isEmpty() {
            return segments.isEmpty();
        }
    }

    static class PositionedSegment {
        String text;
        StellardownParser.Style style;
        int x;

        public PositionedSegment(String trimmed, StellardownParser.Style style, int cursorX) {
            this.text = trimmed;
            this.style = style;
            this.x = cursorX;
        }
    }

    private int measureWord(String text, StellardownParser.Style style) {
        Component component = StellardownRenderer.toComponent(text, style);
        return font.width(component);
    }


    public static MutableComponent toComponent(String text, StellardownParser.Style style) {
        MutableComponent comp = Component.literal(text);

        if (style.bold) comp.withStyle(ChatFormatting.BOLD);
        if (style.italic) comp.withStyle(ChatFormatting.ITALIC);
        if (style.underline) comp.withStyle(ChatFormatting.UNDERLINE);
        if (style.strikethrough) comp.withStyle(ChatFormatting.STRIKETHROUGH);
        if (style.obfuscated) {
            Stellaris.LOG.error("zffsf");
            comp.withStyle(ChatFormatting.OBFUSCATED);
        }

        if (style.color != null) comp.withColor(Utils.getMinecraftColor(style.color));
        if (style.ref != null) comp.withColor(Utils.getMinecraftColor("blue"));


        return comp;
    }

}
