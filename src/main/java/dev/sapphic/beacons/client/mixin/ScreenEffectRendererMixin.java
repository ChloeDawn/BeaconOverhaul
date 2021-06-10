package dev.sapphic.beacons.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
abstract class ScreenEffectRendererMixin {
  @Inject(method = "renderFire", require = 1, allow = 1, at = @At("HEAD"), cancellable = true)
  private static void omitFireOverlayIfResistant(
    final Minecraft minecraft, final PoseStack stack, final CallbackInfo ci
  ) {
    if ((minecraft.player != null) && minecraft.player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
      ci.cancel();
    }
  }
}
