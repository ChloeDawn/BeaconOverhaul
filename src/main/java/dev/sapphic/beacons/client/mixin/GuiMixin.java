package dev.sapphic.beacons.client.mixin;

import dev.sapphic.beacons.BeaconMobEffects;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
abstract class GuiMixin {
  @Shadow private int screenHeight;

  @Shadow
  protected abstract Player getCameraPlayer();

  @ModifyVariable(method = "renderPlayerHealth", index = 25,
    at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 1, shift = Shift.BY, by = 5))
  private int noNutritionHungerShake(final int randY) {
    final Player player = this.getCameraPlayer();
    if (!player.getFoodData().needsFood()) {
      if (player.hasEffect(BeaconMobEffects.NUTRITION)) {
        return this.screenHeight - 39;
      }
    }
    return randY;
  }
}
