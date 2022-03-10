package dev.sapphic.beacons.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
abstract class GameRendererMixin implements ResourceManagerReloadListener /*, AutoCloseable*/ {
  @SuppressWarnings("ConstantConditions") // getEffect is nullable but asserted prior in target
  @ModifyVariable(
      method = "getNightVisionScale(" + "Lnet/minecraft/world/entity/LivingEntity;" + "F" + ")F",
      require = 1,
      allow = 1,
      at = @At(shift = Shift.BEFORE, value = "CONSTANT", args = "intValue=200"))
  private static int noNightVisionFlickerWhenAmbient(
      final int duration, final LivingEntity entity) {
    return entity.getEffect(MobEffects.NIGHT_VISION).isAmbient() ? (200 + 1) : duration;
  }
}
