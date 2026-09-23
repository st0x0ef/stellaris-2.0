package org.exodusstudio.stellaris.fabric.mixin;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.exodusstudio.stellaris.platform.fabric.RegistryAliases;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {

    @Shadow
    @Final
    private ResourceKey<? extends Registry<T>> key;

    @ModifyVariable(method = {
            "get(Lnet/minecraft/resources/Identifier;)Ljava/util/Optional;",
            "getValue(Lnet/minecraft/resources/Identifier;)Ljava/lang/Object;",
            "containsKey(Lnet/minecraft/resources/Identifier;)Z"
    }, at = @At("HEAD"), argsOnly = true)
    private Identifier stellaris$resolveAlias(Identifier id) {
        return RegistryAliases.resolve(this.key, id);
    }

    @ModifyVariable(method = {
            "get(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;",
            "getValue(Lnet/minecraft/resources/ResourceKey;)Ljava/lang/Object;",
            "containsKey(Lnet/minecraft/resources/ResourceKey;)Z"
    }, at = @At("HEAD"), argsOnly = true)
    private ResourceKey<T> stellaris$resolveAliasKey(ResourceKey<T> resourceKey) {
        Identifier resolved = RegistryAliases.resolve(this.key, resourceKey.identifier());
        return resolved == resourceKey.identifier() ? resourceKey : ResourceKey.create(this.key, resolved);
    }
}
