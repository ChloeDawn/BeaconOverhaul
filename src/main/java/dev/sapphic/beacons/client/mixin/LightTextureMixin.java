package dev.sapphic.beacons.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.Nullable;
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

  @ModifyVariable(method = "updateLightTexture(F)V",
    index = 12, require = 1, allow = 1,
    at = @At(shift = Shift.BEFORE, value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lcom/mojang/math/Vector3f;set(FFF)V"))
  private float fullBrightNightVision(final float skyLight) {
    final @Nullable var player = this.minecraft.player;

    if (player != null) {
      final @Nullable var nightVision = player.getEffect(MobEffects.NIGHT_VISION);

      if ((nightVision != null) && (nightVision.getAmplifier() > 0)) {
        return 15.0F;
      }
    }

    return skyLight;
  }
}
