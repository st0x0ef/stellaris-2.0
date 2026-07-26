package org.exodusstudio.stellaris.client.markdown;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import org.exodusstudio.stellaris.Stellaris;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MarkdownPage {


    public static String METADATA_DELIMITOR = "---";

    public Identifier id;
    public String title;
    public String entryId;
    public IconType iconType;


    public List<Either<TagKey<Block>, ResourceKey<Block>>> associatedBlocks = new ArrayList<>();
    //Contains all the metadata too
    public final String rawContent;
    //Only the rendered part
    public String content;

    public MarkdownPage(Identifier id,  String rawContent) {
        this.id = id;
        this.rawContent = rawContent;
        this.content = scanMetadata();
    }

    public String scanMetadata()  {

        List<String> lines;

        try {
            lines = new StringReader(rawContent).readAllLines();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        int metadataPart = 0;
        int metadataLine = 0;

        for (String line : lines) {
            metadataLine++;

            //We check if the metadata are finished
            if (line.equals(METADATA_DELIMITOR)) {
                metadataPart++;
                if(metadataPart == 2) {
                    break;
                }
            }

            if (line.startsWith("title:")) {
                this.title = line.substring(6).trim();
            }

            if (line.startsWith("iconType:")) {
                this.iconType = IconType.fromString(line.substring(9).trim());
            }

            if (line.startsWith("entryId:")) {
                this.entryId = line.substring(8).trim();
            }

            if(line.startsWith("associatedBlocks:")) {
                String list = line.substring(17).trim();
                list = list.replace("[", "").replace("]", "");
                String[] blocks = list.split(",");
                for (String block : blocks) {
                    block = block.trim();
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

        StringBuilder builder = new StringBuilder();

        for(int i = metadataLine; i < lines.size(); i++) {
            // Process the content lines after metadata
            builder.append(lines.get(i)).append("\n");
        }

        return builder.toString();
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
