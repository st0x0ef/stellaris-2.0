package org.exodusstudio.stellaris.common.data.wiki;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.utils.stellardown.StellardownParser;
import org.exodusstudio.stellaris.client.utils.stellardown.StellardownStyle;
import org.jspecify.annotations.NonNull;
import oshi.util.tuples.Pair;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MarkdownPage {


    public static String METADATA_DELIMITER = "---";

    public Identifier id;
    public String title;
    public Identifier entryId;
    public IconType iconType;


    public static final Codec<MarkdownPage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter((m) -> m.id),
            Codec.STRING.fieldOf("rawContent").forGetter((m) -> m.rawContent)
    ).apply(instance, MarkdownPage::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MarkdownPage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, (m) -> m.id,
            ByteBufCodecs.STRING_UTF8, (m) -> m.rawContent,
            MarkdownPage::new
    );


    public List<Either<TagKey<Block>, ResourceKey<Block>>> associatedBlocks = new ArrayList<>();
    //Contains all the metadata too
    public final String rawContent;
    //Only the rendered part
    public String content;

    //We already parse the content to have access to the items/entity rendered
    private final List<Pair<String, StellardownParser.Style>> segments;

    public MarkdownPage(Identifier id,  String rawContent) {
        this.id = id;
        this.rawContent = rawContent;
        this.content = scanMetadata();
        StellardownParser parser = new StellardownParser();
        this.segments = parser.parse(parser.tokenize(this.content));
    }

    public String scanMetadata()  {
        List<String> lines;

        try {
            lines = new StringReader(rawContent).readAllLines();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        boolean insideMetadata = false;
        int contentStart = 0;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            if (line.contains(METADATA_DELIMITER.trim())) {
                if (!insideMetadata) {
                    insideMetadata = true;
                } else {
                    contentStart = i + 1;
                    break;
                }
                continue;
            }

            if (insideMetadata) {
                parseMetadataLine(line);
            }
        }


        StringBuilder builder = new StringBuilder();

        for(int i = contentStart; i < lines.size(); i++) {
            // Process the content lines after metadata
            builder.append(lines.get(i)).append("\n");
        }

        return builder.toString();
    }

    public void parseMetadataLine(String line) {
        if (line.startsWith("title:")) {
            this.title = line.substring(6).trim();
        }

        if (line.startsWith("iconType:")) {
            this.iconType = IconType.fromString(line.substring(9).trim());
        }

        if (line.startsWith("entryId:")) {
            this.entryId = Identifier.parse(line.substring(8).trim());
        }

        if(line.startsWith("associatedBlocks:")) {
            String list = line.substring(17).trim();
            list = list.replace("[", "").replace("]", "");
            String[] blocks = list.split(",");
            for (String block : blocks) {
                block = block.trim();
                if (!block.isEmpty()) {
                    if (block.startsWith("#")) {
                        // It's a tag
                        this.associatedBlocks.add(Either.left(TagKey.create(Registries.BLOCK, Identifier.parse(block.substring(1)))));
                    } else {
                        // It's a resource key
                        this.associatedBlocks.add(Either.right(ResourceKey.create(Registries.BLOCK, Identifier.parse(block))));
                    }
                }
            }
        }

    }

    public StellardownStyle.EntityStyle getEntityIcon() {
        for (Pair<String, StellardownParser.Style> segment : segments) {
            StellardownParser.Style style = segment.getB();
            if(style.entityStyle != null) {
                return style.entityStyle;
            }
        }
        return null;
    }

    public StellardownStyle.ItemStyle getItemIcon() {
        for (Pair<String, StellardownParser.Style> segment : segments) {
            StellardownParser.Style style = segment.getB();
            if(style.itemStyle != null) {
                return style.itemStyle;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Metadata : \n ---\n"
                + "entryId: " + this.entryId
                + "\ntitle: " + this.title
                + "\niconType: " + this.iconType
                + "\nassociatedBlocks: " + this.associatedBlocks
                + "\n---\n\n"
                + this.content;
    }

    public enum IconType implements StringRepresentable {
        ENTITY,
        ITEM;

        public static final Codec<IconType> CODEC = StringRepresentable.fromEnum(IconType::values);


        @Override
        public @NonNull String getSerializedName() {
            return switch (this) {
                case ENTITY -> "entity";
                case ITEM -> "item";
            };
        }

        public static IconType fromString(String s) {
            return switch (s.toLowerCase()) {
                case "entity" -> ENTITY;
                case "item" -> ITEM;
                default -> {
                    Stellaris.LOG.error("Unknown icon type: {}", s);
                    yield ENTITY;
                }
            };
        }
    }


}
