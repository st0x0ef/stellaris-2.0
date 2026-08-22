package org.exodusstudio.stellaris.client.utils.stellardown;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.client.utils.ActionBox;
import org.exodusstudio.stellaris.client.utils.ClientUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.joml.Matrix3x2fStack;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StellardownRenderer {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\h*\\S+\\h*|\\h+");

    private final Font font;
    private final StellardownParser parser;
    private final List<Pair<String, StellardownParser.Style>> segments;
    private List<Line> renderedLines;

    private int layoutHeight;

    private int areaWidth;

    public StellardownRenderer(int areaWidth, Font font, List<Pair<String, StellardownParser.Style>> segments) {
        this.font = font;
        this.parser = new StellardownParser();

        this.segments = segments;
        this.areaWidth = areaWidth;
        updateLayout(areaWidth);

    }

    public StellardownRenderer(String formattedText, int areaWidth, Font font) {
        this.font = font;
        this.parser = new StellardownParser();

        //We only parse one time the text
        this.segments = parser.parse(parser.tokenize(formattedText));
        this.areaWidth = areaWidth;

        updateLayout(areaWidth);
    }



    /**
     * Used to update the layout. Useful when window is resized or when the text is dynamic. It will re-layout the text according to the new area width.
     *
     * @param areaWidth the new width of the area where the text is rendered
     */
    public void updateLayout(int areaWidth) {
        if (areaWidth == this.areaWidth && renderedLines != null) {
            return;
        }

        this.areaWidth = areaWidth;
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

                StellardownStyle.ImageStyle image = segment.getB().image;

                if (needsNewLine(image.getWidth(), cursorX, areaWidth)) {
                    lines.add(currentLine);
                    currentLine = new Line();
                    cursorX = 0;
                }

                currentLine.add(new ImageSegment(image, cursorX));

                cursorX += image.getWidth();

                continue;
            }

            // -------------------------------------------------------
            // ENTITY
            // -------------------------------------------------------

            if (segment.getB().entityStyle != null) {

                StellardownStyle.EntityStyle entity = segment.getB().entityStyle;

                if (needsNewLine(entity.getWidth(), cursorX, areaWidth)) {
                    lines.add(currentLine);
                    currentLine = new Line();
                    cursorX = 0;
                }

                currentLine.add(new EntitySegment(entity, cursorX));

                cursorX += entity.getWidth();

                continue;
            }

            // -------------------------------------------------------
            // ITEM
            // -------------------------------------------------------

            if (segment.getB().itemStyle != null && !segment.getB().itemStyle.onlyIcon) {

                StellardownStyle.ItemStyle item = segment.getB().itemStyle;

                int width = item.scale * 16;

                if (needsNewLine(item.getWidth(), cursorX, areaWidth)) {
                    lines.add(currentLine);
                    currentLine = new Line();
                    cursorX = 0;
                }

                currentLine.add(new ItemSegment(item, cursorX));

                cursorX += width;

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

                currentLine.add(new TextSegment(
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
            for (PositionnedSegment seg : line.segments) {
                
                
                switch (seg) {
                    case TextSegment textSeg -> {
                        MutableComponent component = toComponent(textSeg.text, textSeg.style);

                        int textX = x + textSeg.x;
                        int textY = y + line.y;

                        if(textSeg.style.ref != null) {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("ref", textSeg.style.ref);
                            clickBoxConsumer.accept(new ActionBox(textX, textY, textSeg.width, textSeg.height, textSeg.style.ref, data));
                        }

                        if(textSeg.style.tooltip != null) {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("tooltip", textSeg.style.tooltip);
                            clickBoxConsumer.accept(new ActionBox(textX, textY, textSeg.width, textSeg.height, textSeg.style.tooltip, data));
                        }

                        guiGraphics.text(
                                font,
                                component,
                                textX,
                                textY,
                                Utils.getMinecraftColor("white"));
                    }
                    case ImageSegment imageSeg -> {
                        int imageX = x + imageSeg.x;

                        if(imageSeg.image.centered) {
                            imageX = x + this.areaWidth / 2 - (imageSeg.getWidth() / 2);
                        }

                        guiGraphics.blit(
                                RenderPipelines.GUI_TEXTURED,
                                imageSeg.image.texture,
                                imageX,
                                y + line.y ,
                                0,
                                0,
                                imageSeg.getWidth(),
                                imageSeg.getHeight(),
                                imageSeg.getWidth(),
                                imageSeg.getHeight());
                    }
                    case EntitySegment entitySeg -> {
                        StellardownStyle.EntityStyle entityStyle = entitySeg.entity;

                        Entity entity = ClientUtils.createEntity(Minecraft.getInstance().level, entityStyle.identifier);

                        if(entity instanceof LivingEntity livingEntity) {


                            int left = x + entitySeg.x;
                            int right = left + entitySeg.getWidth();

                            if (entityStyle.centered) {
                                left = x  + entitySeg.x + this.areaWidth / 2  - (entitySeg.getWidth() / 4);
                            }

                            int top = y + line.y ;

                            int bottom = top + entitySeg.getHeight();

                            ClientUtils.renderEntityInGui(guiGraphics, left, top, right, bottom, entityStyle.scale, 0.25F, mouseX, mouseY, livingEntity, entityStyle.rotation);
                        }
                    }
                    case ItemSegment itemSeg -> {
                        StellardownStyle.ItemStyle itemStyle = itemSeg.item;

                        Matrix3x2fStack matrixStack = guiGraphics.pose();
                        matrixStack.pushMatrix();

                        float scale = itemStyle.scale;
                        int itemSize = 16;

                        int yPos = y + line.y;

                        float tx = x;

                        if (itemStyle.centered) {
                            tx = x + this.areaWidth / 2f - (scale * itemSize / 2f);
                        }

                        matrixStack.translate(tx, yPos);
                        matrixStack.scale(scale, scale);

                        guiGraphics.item(new ItemStack(itemStyle.getItem()), 0, 0);

                        matrixStack.popMatrix();
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + seg);
                }
                
            }
        }

        return layoutHeight;
    }

    private boolean needsNewLine(int contentWidth, int cursorX, int width) {
        return cursorX > 0 &&
                cursorX + contentWidth > width;
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

    public int getLayoutHeight() {
        return this.layoutHeight;
    }
    
    public interface PositionnedSegment{
        
        int getX();
        int getWidth();
        int getHeight();
        
    }
    
    public record TextSegment(String text, StellardownParser.Style style, int x, int width, int height) implements PositionnedSegment {
        @Override
        public int getX() {
            return x;
        }
        @Override
        public int getWidth() {
            return width;
        }
        @Override
        public int getHeight() {
            return height;
        }
    }

    public record ImageSegment(StellardownStyle.ImageStyle image, int x) implements PositionnedSegment {
        @Override
        public int getX() {
            return x;
        }
        @Override
        public int getWidth() {
            return image.getWidth();
        }
        @Override
        public int getHeight() {
            return image.getHeight();
        }
    }

    public record EntitySegment(StellardownStyle.EntityStyle entity, int x) implements PositionnedSegment {
        @Override
        public int getX() {
            return x;
        }
        @Override
        public int getWidth() {
            return entity.getWidth();
        }
        @Override
        public int getHeight() {
            return entity.getHeight();
        }
    }
    
    public record ItemSegment(StellardownStyle.ItemStyle item, int x)  implements PositionnedSegment {

        @Override
        public int getX() {
            return x;
        }
        @Override
        public int getWidth() {
            return item.getWidth();
        }
        @Override
        public int getHeight() {
            return item.getHeight();
        }
    }

    public class Line {

        List<PositionnedSegment> segments = new ArrayList<>();

        int totalWidth;
        int height = font.lineHeight;
        int y;

        public void add(PositionnedSegment segment) {

            segments.add(segment);

            totalWidth = segment.getX() + segment.getWidth();
            height = Math.max(height, segment.getHeight());
        }
    }

}
