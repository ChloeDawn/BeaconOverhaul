package dev.sapphic.beacons.client.mixin;

import dev.sapphic.beacons.BeaconMobEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
abstract class GuiMixin {
  @Shadow
  private int screenHeight;

  @Shadow
  private Player getCameraPlayer() {
    throw new AssertionError();
  }

  @ModifyVariable(
      method = "renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V",
      at = @At(
          target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
          ordinal = 0, shift = Shift.BY, by = 5,value = "INVOKE", opcode = Opcodes.INVOKEINTERFACE),
      index = 24, require = 1, allow = 1)
  private int noNutritionHungerShake(final int randY) {
    final var player = this.getCameraPlayer();

    if ((player != null) && !player.getFoodData().needsFood()) {
      if (player.hasEffect(BeaconMobEffects.NUTRITION)) {
        return this.screenHeight - 39;
      }
    }

    return randY;
  }
}
