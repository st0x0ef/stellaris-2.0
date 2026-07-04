package org.exodusstudio.stellaris.common.entities.alien;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;

public class AlienEntity extends Villager {

    public AlienEntity(EntityType<? extends Villager> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Villager.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    public Villager getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        AlienEntity child = EntityTypesRegistry.ALIEN.get().create(level, EntitySpawnReason.BREEDING);
        if (child != null) {
            child.finalizeSpawn(level, level.getCurrentDifficultyAt(child.blockPosition()),
                    EntitySpawnReason.BREEDING, null);
        }
        return child;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        EntitySpawnReason reason, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData);

        // Aliens take on a biome-appropriate type, and — when spawned without a pre-assigned job
        // (e.g. natural/spawn-egg/wanderer spawns) — a random profession, so they immediately have
        // trades even without a job-site block. Structure-embedded aliens are placed from NBT with a
        // baked-in profession; StructureTemplate calls finalizeSpawn on them, so we must NOT overwrite
        // an already-assigned profession here or the baked job would be lost.
        RegistryAccess registryAccess = level.registryAccess();
        VillagerData data = this.getVillagerData()
                .withType(registryAccess, VillagerType.byBiome(level.getBiome(this.blockPosition())));

        if (data.profession().is(VillagerProfession.NONE)) {
            data = data.withProfession(registryAccess, AlienJobs.random(this.random));
        }

        this.setVillagerData(data);

        return result;
    }

    @Override
    protected void updateTrades(ServerLevel level) {
        VillagerData data = this.getVillagerData();
        data.profession().unwrapKey().ifPresent(professionKey -> {
            ResourceKey<TradeSet> tradeSetKey = ResourceKey.create(
                    Registries.TRADE_SET,
                    IdentifierUtils.id("alien/" + professionKey.identifier().getPath() + "/" + data.level()));

            boolean hasAlienTrades = level.registryAccess()
                    .lookupOrThrow(Registries.TRADE_SET)
                    .get(tradeSetKey)
                    .isPresent();

            if (hasAlienTrades) {
                this.addOffersFromTradeSet(level, this.getOffers(), tradeSetKey);
            }
        });
    }

    @Override
    public void spawnGolemIfNeeded(ServerLevel level, long time, int villagersNearby) {
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (Stellaris.CONFIG != null && !Stellaris.CONFIG.alienConfig.enableAlienSpawn) {
            this.discard();
        }
    }
}
