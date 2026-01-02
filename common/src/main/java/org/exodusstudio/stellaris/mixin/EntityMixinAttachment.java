package org.exodusstudio.stellaris.mixin;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.exodusstudio.stellaris.common.entities.EntityDataAttachmentAccessor;
import org.exodusstudio.stellaris.platform.DataAttachmentsPlatform;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixinAttachment implements EntityDataAttachmentAccessor {


    @Override
    public boolean hasDataAttachments(Identifier key) {
        Entity entity = (Entity)(Object)this;
        return DataAttachmentsPlatform.hasEntityData(entity, key);
    }

    @Override
    public <T> void saveDataAttachments(Identifier key, T value) {
        Entity entity = (Entity)(Object)this;
        DataAttachmentsPlatform.saveEntityData(entity, key, value);
    }

    @Override
    public <T> T getDataAttachments(Identifier key, Class<T> clazz) {
        Entity entity = (Entity)(Object)this;
        return DataAttachmentsPlatform.getEntityData(entity, key, clazz);
    }
}
