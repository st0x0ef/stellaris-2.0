package org.exodusstudio.stellaris.common.entities.alien;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

/**
 * The 13 vanilla villager professions an Alien can take on. Ported from Beyond Earth's
 * {@code AlienJobs}. Aliens reuse vanilla professions so they benefit from the vanilla brain,
 * job-site logic and per-profession textures, while their trades are sourced from
 * alien-specific trade sets (see {@link AlienEntity#updateTrades}).
 */
public enum AlienJobs {
    FARMER(VillagerProfession.FARMER),
    FISHERMAN(VillagerProfession.FISHERMAN),
    SHEPHERD(VillagerProfession.SHEPHERD),
    FLETCHER(VillagerProfession.FLETCHER),
    LIBRARIAN(VillagerProfession.LIBRARIAN),
    CARTOGRAPHER(VillagerProfession.CARTOGRAPHER),
    CLERIC(VillagerProfession.CLERIC),
    ARMORER(VillagerProfession.ARMORER),
    WEAPONSMITH(VillagerProfession.WEAPONSMITH),
    TOOLSMITH(VillagerProfession.TOOLSMITH),
    BUTCHER(VillagerProfession.BUTCHER),
    LEATHERWORKER(VillagerProfession.LEATHERWORKER),
    MASON(VillagerProfession.MASON);

    private static final AlienJobs[] VALUES = values();

    public final ResourceKey<VillagerProfession> profession;

    AlienJobs(ResourceKey<VillagerProfession> profession) {
        this.profession = profession;
    }

    public static ResourceKey<VillagerProfession> random(RandomSource random) {
        return VALUES[random.nextInt(VALUES.length)].profession;
    }
}
