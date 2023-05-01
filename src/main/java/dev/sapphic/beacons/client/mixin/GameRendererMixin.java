package dev.sapphic.beacons.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
abstract class GameRendererMixin implements ResourceManagerReloadListener /*, AutoCloseable*/ {
  @Inject(
      method = "getNightVisionScale(" + "Lnet/minecraft/world/entity/LivingEntity;" + "F" + ")F",
      require = 1,
      allow = 1,
      at = @At(shift = Shift.BY, by = -2, value = "CONSTANT", args = "intValue=200"),
      locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true)
  private static void noNightVisionFlickerWhenAmbient(
      final LivingEntity entity, final float partialTick,
      final CallbackInfoReturnable<Float> cir, final MobEffectInstance effect) {
    if (effect.isAmbient()) {
      cir.setReturnValue(1.0F);
    }
  }
}
