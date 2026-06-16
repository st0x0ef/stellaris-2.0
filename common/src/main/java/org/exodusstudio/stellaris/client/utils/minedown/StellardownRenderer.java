package org.exodusstudio.stellaris.client.utils.minedown;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    private final StellardownParser parser;
    private final List<Pair<String, StellardownParser.Style>> segments;
    private List<Line> renderedLines;

    public StellardownRenderer(String formattedText, int areaWidth, Font font) {
        this.font = font;
        this.parser = new StellardownParser();

        //We only parse one time the text
        this.segments = parser.parse(parser.tokenize(formattedText));


        updateLayout(areaWidth);
    }

    /**
     * Used to update the layout. Useful when window is resized or when the text is dynamic. It will re-layout the text according to the new area width.
     * @param areaWidth the new width of the area where the text is rendered
     */
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

                // Split any token that contains an explicit break tag or newline so that [br] works when attached to words
                for (String part : splitByBrAndNewline(actual)) {
                    if (part.isEmpty()) continue;

                    if (part.equals("\n") || part.equals("[br]")) {              // explicit line break
                        lines.add(currentLine);
                        currentLine = new Line();
                        cursorX = 0;

                    } else {
                        int wordWidth = measureWord(part, segment.getB());

                        if (cursorX + wordWidth > areaWidth && cursorX > 0) {  // wrap
                            lines.add(currentLine);
                            currentLine = new Line();
                            cursorX = 0;
                            String trimmed = part.stripLeading();
                            int trimmedWidth = measureWord(trimmed, segment.getB());

                            currentLine.add(new PositionedSegment(trimmed, segment.getB(), cursorX), trimmedWidth);
                            cursorX += trimmedWidth;

                        } else {
                            currentLine.add(new PositionedSegment(part, segment.getB(), cursorX), wordWidth);
                            cursorX += wordWidth;
                        }
                    }
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

    // Helper: splits a string into parts around [br] or newline, keeping the delimiters as separate parts
    private List<String> splitByBrAndNewline(String s) {
        List<String> parts = new ArrayList<>();
        Pattern brPattern = Pattern.compile("\\[br]|\\n");
        Matcher m = brPattern.matcher(s);
        int last = 0;
        while (m.find()) {
            if (m.start() > last) parts.add(s.substring(last, m.start()));
            parts.add(m.group());
            last = m.end();
        }
        if (last < s.length()) parts.add(s.substring(last));
        return parts;
    }

    public int render(int x, int y, GuiGraphicsExtractor guiGraphics, Consumer<ActionBox> clickBoxConsumer) {
        for (Line line : this.renderedLines) {
            for (PositionedSegment seg : line.segments) {

                MutableComponent component = toComponent(seg.text, seg.style);


                if(seg.style.ref != null) {
                    //component.withStyle(s -> s.withHoverEvent(new HoverEvent.ShowText(Component.literal("entry: " + seg.style.ref))));

                    int width = measureWord(seg.text, seg.style);
                    clickBoxConsumer.accept(new ActionBox(x + seg.x, y + line.y, width, line.height, null, (info) -> info.actionBox().changePage(info.infoWidget(), seg.style.ref), seg.style.ref));
                }

                guiGraphics.text(font, component,
                        x + seg.x, y + line.y,  // use pre-computed line.y
                        Utils.getMinecraftColor("white"));

            }
        }

        if (renderedLines.isEmpty()) return 0;
        Line lastLine = renderedLines.getLast();
        return lastLine.y + lastLine.height;
    }

    public int render(int x, int y, GuiGraphicsExtractor guiGraphics) {
        return render(x, y, guiGraphics, ignored -> {});
    }


    public class Line {
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


        if(style.translatable) {
            comp = Component.translatable(text);
        }

        //We automatically make reference text blue, but we can also specify a custom color if needed

        if (style.bold) comp.withStyle(ChatFormatting.BOLD);
        if (style.italic) comp.withStyle(ChatFormatting.ITALIC);
        if (style.underline) comp.withStyle(ChatFormatting.UNDERLINE);
        if (style.strikethrough) comp.withStyle(ChatFormatting.STRIKETHROUGH);
        if (style.obfuscated) comp.withStyle(ChatFormatting.OBFUSCATED);

        if (style.color != null) comp.withColor(Utils.getMinecraftColor(style.color));

        if (style.ref != null) {
            comp.withStyle(ChatFormatting.UNDERLINE);
            comp.withColor(Utils.getMinecraftColor("coral"));
        }
        return comp;
    }

}
