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
  @Inject(
      method = "renderFire(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
      at = @At("HEAD"), require = 1, allow = 1, cancellable = true)
  private static void omitFireOverlayIfResistant(final Minecraft mc, final PoseStack stack, final CallbackInfo ci) {
    if ((mc.player != null) && mc.player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
      ci.cancel();
    }
  }
}
