package org.exodusstudio.stellaris.client.utils.stellardown;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.joml.Vector3f;

public abstract class StellardownStyle {

    public boolean centered = true;
    public int width;
    public int height;

    public void parseDefaultParam(String param) {

        if(param.equals("center") ) {
            centered = true;
        } else if(param.startsWith("centered=") ) {
            centered = Boolean.parseBoolean(param.substring("centered=".length()));
        }
        if(param.startsWith("width=") ) {
            width = Integer.parseInt(param.substring("width=".length()));
        }
        if(param.startsWith("height=") ) {
            height = Integer.parseInt(param.substring("height=".length()));
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public static class ImageStyle extends StellardownStyle {

        public Identifier texture;

        public ImageStyle(Identifier texture) {
            this.texture = texture;
        };

        public static ImageStyle parse(String content) {
            String[] params = content.split(" "); //all the params are separated with a space

            Identifier imageId = Identifier.parse(params[0]);
            ImageStyle image = new ImageStyle(imageId);

            for(int i = 1; i < params.length; i++) {
                String param = params[i];
                image.parseDefaultParam(param);
            }
            return image;
        }


    }

    public static class ItemStyle extends StellardownStyle {

        public Identifier identifier;
        public int scale = 2;
        public boolean onlyIcon = false;

        public ItemStyle(Identifier identifier) {
            this.identifier = identifier;
        };


        public static ItemStyle parse(String content) {
            String[] params = content.split(" "); //all the params are separated with a space

            Identifier itemId = Identifier.parse(params[0]);
            ItemStyle style = new ItemStyle(itemId);

            for(int i = 1; i < params.length; i++) {

                String param = params[i];
                style.parseDefaultParam(param);

                if(param.startsWith("scale=") ) {
                    style.scale = Integer.parseInt(param.substring("scale=".length()));
                }
                if(param.startsWith("onlyIcon") ) {
                    style.onlyIcon = true;
                }
            }
            return style;
        }

        public int getWidth() {
            return 16 * scale;
        }

        public int getHeight() {
            return 16 * scale;
        }

        public Item getItem() {
            return BuiltInRegistries.ITEM.getValue(identifier);
        }
    }

    public static class EntityStyle extends StellardownStyle {
        public Identifier identifier;
        public int scale = 10;
        public Vector3f rotation = new Vector3f(0, 0, 0);

        public EntityStyle(Identifier identifier) {
            this.identifier = identifier;
            this.width = 50;
            this.height = 50;
        }

        public static EntityStyle parse(String content) {
            String[] params = content.split(" "); //all the params are separated with a space

            Identifier entityId = Identifier.parse(params[0]);
            EntityStyle style = new EntityStyle(entityId);

            for(int i = 1; i < params.length; i++) {

                String param = params[i];


                style.parseDefaultParam(param);

                if(param.startsWith("scale=") ) {
                    style.scale = Integer.parseInt(param.substring("scale=".length()));
                }
                if(param.startsWith("height=") ) {
                    style.height = Integer.parseInt(param.substring("height=".length()));
                }
                if(param.startsWith("rotation=") ) {
                    String onlyDigits = param.substring("scale=".length()).trim().replace("[", "").replace("]", "");
                    // Parse the rotation values (assuming they are comma-separated)
                    String[] rotationValues = onlyDigits.split(",");
                    if (rotationValues.length == 3) {
                        style.rotation = new Vector3f(Float.parseFloat(rotationValues[0]), Float.parseFloat(rotationValues[1]), Float.parseFloat(rotationValues[2]));
                    }
                }
            }
            return style;
        }
    }


}
