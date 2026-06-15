package org.exodusstudio.stellaris.common.vehicle_upgrade;

import com.mojang.serialization.Codec;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

public class FuelType {

    public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

    public static float getMegametersTraveled(int fuelQuantity, Type type) {
        if (type != null) {
            return switch (type) {
                case FUEL ->
                    19.22f * fuelQuantity; // Need 20mb to go on Moon, 2133mb to go on Venus, 2900mb to go on Mars and
                                           // 4786mb to go on Mercury (approx)
                case DIESEL -> 0.0F;
                case HYDROGEN ->
                    21.36f * fuelQuantity; // Need 18mb to go on Moon, 1920mb to go on Venus, 2610mb to go on Mars and
                                           // 4307mb to go on Mercury (approx)
                case RADIOACTIVE, URANIUM ->
                    23.74f * fuelQuantity; // Need 16mb to go on Moon, 1728mb to go on Venus, 2349mb to go on Mars and
                                           // 3876mb to go on Mercury (approx)
                case NEPTUNIUM ->
                    26.38f * fuelQuantity; // Need 15mb to go on Moon, 1555mb to go on Venus, 2114mb to go on Mars and
                                           // 3488mb to go on Mercury (approx)
                case PLUTONIUM ->
                    29.3f * fuelQuantity; // Need 14mb to go on Moon, 1400mb to go on Venus, 1903mb to go on Mars and
                                          // 3140mb to go on Mercury (approx)
            };
        }

        return 0.0f;
    }

    public static float getFuelNeededToGoOnPlanet(Planet actual, Planet destination, Type type) {
        float distance = 100.0f; // Placeholder distance since Planet doesn't have distanceFromEarth

        if (type != null) {
            return switch (type) {
                case FUEL ->
                    distance / 19.22f; // Need 20mb to go on Moon, 2133mb to go on Venus, 2900mb to go on Mars and
                                       // 4786mb to go on Mercury (approx)
                case DIESEL -> Float.MAX_VALUE; // Diesel is not used for space travel in rocket
                case HYDROGEN ->
                    distance / 21.36f; // Need 18mb to go on Moon, 1920mb to go on Venus, 2610mb to go on Mars and
                                       // 4307mb to go on Mercury (approx)
                case RADIOACTIVE, URANIUM ->
                    distance / 23.74f; // Need 16mb to go on Moon, 1728mb to go on Venus, 2349mb to go on Mars and
                                       // 3876mb to go on Mercury (approx)
                case NEPTUNIUM ->
                    distance / 26.38f; // Need 15mb to go on Moon, 1555mb to go on Venus, 2114mb to go on Mars and
                                       // 3488mb to go on Mercury (approx)
                case PLUTONIUM ->
                    distance / 29.3f; // Need 14mb to go on Moon, 1400mb to go on Venus, 1903mb to go on Mars and
                                      // 3140mb to go on Mercury (approx)
            };
        }

        return Float.MAX_VALUE;
    }

    public static Item getItemBasedOnLoacation(Identifier location) {
        return ItemsRegistry.ITEMS.getRegistrar().get(location);
    }

    public enum Type implements StringRepresentable {
        FUEL(GUISprites.FUEL_OVERLAY, null),
        DIESEL(GUISprites.DIESEL_OVERLAY, null),
        HYDROGEN(GUISprites.HYDROGEN_OVERLAY, null),
        RADIOACTIVE(GUISprites.SIDEWAYS_ENERGY_FULL, null),
        URANIUM(GUISprites.SIDEWAYS_ENERGY_FULL, RADIOACTIVE),
        NEPTUNIUM(GUISprites.SIDEWAYS_ENERGY_FULL, RADIOACTIVE),
        PLUTONIUM(GUISprites.SIDEWAYS_ENERGY_FULL, RADIOACTIVE);

        private final Identifier fuelTexture;
        private final Type motorType;

        Type(Identifier fuelTexture, Type motorType) {
            this.fuelTexture = fuelTexture;
            this.motorType = motorType;
        }

        public static Type getTypeBasedOnItem(Item item) {
            if (item == null) {
                return null;
            }
            if (item == ItemsRegistry.HYDROGEN_BUCKET.get()) {
                return HYDROGEN;
            } else if (item == ItemsRegistry.OIL_BUCKET.get() || item == ItemsRegistry.DIESEL_BUCKET.get()) {
                return DIESEL;
            } else if (item == ItemsRegistry.FUEL_BUCKET.get()) {
                return FUEL;
            }

            return null;
        }

        public static Type fromString(String name) {
            return switch (name) {
                case "fuel" -> FUEL;
                case "diesel" -> DIESEL;
                case "hydrogen" -> HYDROGEN;
                case "uranium" -> URANIUM;
                case "neptunium" -> NEPTUNIUM;
                case "plutonium" -> PLUTONIUM;
                case "radioactive" -> RADIOACTIVE;
                default -> null;
            };
        }

        public Type getMotorType() {
            return Objects.requireNonNullElse(this.motorType, this);
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public Identifier getFuelTexture() {
            return this.fuelTexture;
        }
    }
}
