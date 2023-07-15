package dev.sapphic.beacons.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
abstract class FogRendererMixin {
  @Shadow
  private static float fogRed;

  @Shadow
  private static float fogGreen;

  @Shadow
  private static float fogBlue;

  @Inject(
      method = "setupColor(Lnet/minecraft/client/Camera;F"
          + "Lnet/minecraft/client/multiplayer/ClientLevel;IF)V",
      at = @At(
          target = "Lnet/minecraft/client/renderer/GameRenderer;"
              + "getNightVisionScale(Lnet/minecraft/world/entity/LivingEntity;F)F",
          shift = Shift.BY, by = -4, value = "INVOKE", opcode = Opcodes.INVOKESTATIC),
      require = 1, allow = 1, cancellable = true)
  private static void skipNightVisionColorShift(
      final Camera camera, final float tickDelta, final ClientLevel level, final int renderDistance, final float shade,
      final CallbackInfo info) {
    final @Nullable MobEffectInstance nightVision =
        ((LivingEntity) camera.getEntity()).getEffect(MobEffects.NIGHT_VISION);

    if ((nightVision != null) && (nightVision.getAmplifier() > 0)) {
      RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
      info.cancel();
    }
  }
}
