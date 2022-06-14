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
  @Shadow private int screenHeight;

  @Shadow
  protected abstract Player getCameraPlayer();

  @ModifyVariable(
      method = "renderPlayerHealth(" + "Lcom/mojang/blaze3d/vertex/PoseStack;" + ")V",
      index = 24,
      require = 1,
      allow = 1,
      at =
          @At(
              shift = Shift.BY,
              by = 5,
              ordinal = 0,
              value = "INVOKE",
              opcode = Opcodes.INVOKEINTERFACE,
              target = "Lnet/minecraft/util/RandomSource;" + "nextInt(" + "I" + ")I"))
  private int noNutritionHungerShake(final int randY) {
    final var player = this.getCameraPlayer();

    if (!player.getFoodData().needsFood() && player.hasEffect(BeaconMobEffects.NUTRITION)) {
      return this.screenHeight - 39;
    }

    return randY;
  }
}
