package dev.sapphic.beacons.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(LightTexture.class)
abstract class LightTextureMixin /*implements AutoCloseable*/ {
  @Shadow @Final private Minecraft minecraft;

  @ModifyVariable(
      method = "updateLightTexture(" + "F" + ")V",
      index = 15,
      require = 1,
      allow = 1,
      at =
          @At(
              shift = Shift.BEFORE,
              value = "INVOKE",
              opcode = Opcodes.INVOKESPECIAL,
              ordinal = 0,
              target = "Lorg/joml/Vector3f;" + "<init>(" + "Lorg/joml/Vector3fc;" + ")V"))
  private float fullBrightNightVision(final float skyLight) {
    final @Nullable LocalPlayer player = this.minecraft.player;

    if (player != null) {
      final @Nullable MobEffectInstance nightVision = player.getEffect(MobEffects.NIGHT_VISION);

      if ((nightVision != null) && (nightVision.getAmplifier() > 0)) {
        return 15.0F;
      }
    }

    return skyLight;
  }
}
