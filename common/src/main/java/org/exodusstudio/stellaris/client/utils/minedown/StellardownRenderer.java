package org.exodusstudio.stellaris.client.utils.minedown;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.utils.ActionBox;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StellardownRenderer {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\S+\\h*");

    private final Font font;
    private final StellardownParser parser;
    private final List<Pair<String, StellardownParser.Style>> segments;
    private List<Line> renderedLines;

    private int layoutHeight;

    public StellardownRenderer(String formattedText, int areaWidth, Font font) {
        this.font = font;
        this.parser = new StellardownParser();

        //We only parse one time the text
        this.segments = parser.parse(parser.tokenize(formattedText));

        updateLayout(areaWidth);
    }



    /**
     * Used to update the layout. Useful when window is resized or when the text is dynamic. It will re-layout the text according to the new area width.
     *
     * @param areaWidth the new width of the area where the text is rendered
     */
    public void updateLayout(int areaWidth) {
        this.renderedLines = layout(areaWidth);
    }


    public List<Line> layout(int areaWidth) {

        List<Line> lines = new ArrayList<>();

        Line currentLine = new Line();

        int cursorX = 0;

        for (Pair<String, StellardownParser.Style> segment : segments) {

            // -------------------------------------------------------
            // NEW LINE
            // -------------------------------------------------------

            if ("\n".equals(segment.getA())) {

                lines.add(currentLine);

                currentLine = new Line();

                cursorX = 0;

                continue;
            }

            // -------------------------------------------------------
            // IMAGE
            // -------------------------------------------------------

            if (segment.getB().image != null) {

                StellardownParser.ImageStyle image = segment.getB().image;

                if (cursorX + image.width() > areaWidth && cursorX > 0) {
                    lines.add(currentLine);
                    currentLine = new Line();
                    cursorX = 0;
                }

                currentLine.add(new PositionedSegment(image, cursorX));

                cursorX += image.width();

                continue;
            }

            // -------------------------------------------------------
            // ENTITY
            // -------------------------------------------------------

            if (segment.getB().entityStyle != null) {

                StellardownParser.EntityStyle entity = segment.getB().entityStyle;

                if (cursorX + entity.width() > areaWidth && cursorX > 0) {
                    lines.add(currentLine);
                    currentLine = new Line();
                    cursorX = 0;
                }

                currentLine.add(new PositionedSegment(entity, cursorX));

                cursorX += entity.width();

                continue;
            }

            // -------------------------------------------------------
            // TEXT
            // -------------------------------------------------------

            Matcher matcher = WORD_PATTERN.matcher(segment.getA());

            while (matcher.find()) {

                String word = matcher.group();

                int width = measureWord(word, segment.getB());

                if (cursorX + width > areaWidth && cursorX > 0) {

                    lines.add(currentLine);

                    currentLine = new Line();

                    cursorX = 0;

                    // Remove indentation after wrapping
                    word = word.stripLeading();

                    width = measureWord(word, segment.getB());
                }

                currentLine.add(new PositionedSegment(
                        word,
                        segment.getB(),
                        cursorX,
                        width,
                        font.lineHeight));

                cursorX += width;
            }
        }

        lines.add(currentLine);


        int y = 0;

        for (Line line : lines) {
            line.y = y;
            y += line.height;
        }
        this.layoutHeight = y;
        return lines;
    }

    public int render(int x, int y, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, Consumer<ActionBox> clickBoxConsumer) {
        for (Line line : this.renderedLines) {
            for (PositionedSegment seg : line.segments) {

                if (seg.isImage()) {

                    guiGraphics.blit(
                            RenderPipelines.GUI_TEXTURED,
                            seg.image.identifier(),
                            x + seg.x,
                            y + line.y + (line.height - seg.height) / 2,
                            0,
                            0,
                            seg.width,
                            seg.height,
                            seg.width,
                            seg.height);

                    continue;
                }

                if (seg.isEntity()) {
                    StellardownParser.EntityStyle entityStyle = seg.entity;

                    Entity entity = ClientUtils.createEntity(Minecraft.getInstance().level, entityStyle.identifier());
                    if(entity instanceof LivingEntity livingEntity) {

                        int ENTITY_WIDTH = entityStyle.width();

                        int cornerX = x + seg.x;

                        ClientUtils.renderEntityInGui(guiGraphics, cornerX, y + line.y + (line.height - seg.height) / 2, cornerX + ENTITY_WIDTH, y + line.y + (line.height - seg.height) / 2 + entityStyle.scale() + 30, entityStyle.scale(), 0.25F, mouseX, mouseY, livingEntity, entityStyle.rotation());
                    }
                    // Render entity
                    continue;
                }

                MutableComponent component = toComponent(seg.text, seg.style);

                guiGraphics.text(
                        font,
                        component,
                        x + seg.x,
                        y + line.y + (line.height - font.lineHeight) / 2,
                        Utils.getMinecraftColor("white"));
            }
        }

        Line lastLine = renderedLines.getLast();
        return layoutHeight;
    }

    public int render(int x, int y, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        return render(x, y, guiGraphics, mouseX, mouseY, ignored -> {
        });
    }

    private int measureWord(String text, StellardownParser.Style style) {
        Component component = StellardownRenderer.toComponent(text, style);
        return font.width(component);
    }

    public static MutableComponent toComponent(String text, StellardownParser.Style style) {
        MutableComponent comp = Component.literal(text);


        if (style.translatable) {
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

    static class PositionedSegment {

        String text;
        StellardownParser.Style style;

        StellardownParser.ImageStyle image;
        StellardownParser.EntityStyle entity;

        int x;
        int width;
        int height;

        public PositionedSegment(String text,
                                 StellardownParser.Style style,
                                 int x,
                                 int width,
                                 int height) {
            this.text = text;
            this.style = style;
            this.x = x;
            this.width = width;
            this.height = height;
        }

        public PositionedSegment(StellardownParser.ImageStyle image,
                                 int x) {
            this.text = null;
            this.style = null;
            this.image = image;
            this.x = x;
            this.width = image.width();
            this.height = image.height();
        }

        public PositionedSegment(StellardownParser.EntityStyle entity,
                                 int x) {
            this.text = null;
            this.style = null;
            this.image = null;
            this.entity = entity;
            this.x = x;
            this.width = entity.width();
            this.height = entity.width();
        }

        public boolean isImage() {
            return image != null;
        }

        public boolean isEntity() {
            return entity != null;
        }
    }

    public class Line {

        List<PositionedSegment> segments = new ArrayList<>();

        int totalWidth;
        int height = font.lineHeight;
        int y;

        public void add(PositionedSegment segment) {

            segments.add(segment);

            totalWidth = segment.x + segment.width;
            height = Math.max(height, segment.height);
        }

        boolean isEmpty() {
            return segments.isEmpty();
        }
    }

}
