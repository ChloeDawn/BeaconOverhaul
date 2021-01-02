package dev.sapphic.beacons.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
abstract class FogRendererMixin {
  @Redirect(method = "setupColor", at = @At(value = "INVOKE",
    target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z",
    ordinal = 1), require = 1, allow = 1)
  private static boolean hasOnlyBaseNightVision(final LivingEntity entity, final MobEffect effect) {
    final @Nullable MobEffectInstance nightVision = entity.getEffect(effect);
    return (nightVision != null) && (nightVision.getAmplifier() < 1);
  }
}
