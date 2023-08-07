package dev.sapphic.beacons.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
  @Unique
  private @MonotonicNonNull Float baseUpStep;

  @Unique
  private boolean stepIncreased;

  LivingEntityMixin(final EntityType<?> type, final Level level) {
    super(type, level);
  }

  @Inject(method = "baseTick()V", at = @At("HEAD"), require = 1)
  private void setBaseUpStep(final CallbackInfo ci) {
    if (this.baseUpStep == null) {
      this.baseUpStep = this.maxUpStep();
    }
  }

  @Shadow
  public abstract boolean hasEffect(final MobEffect effect);

  @Inject(method = "tickEffects()V", at = @At("HEAD"), require = 1)
  private void updateJumpBoostStepAssist(final CallbackInfo ci) {
    if (this.hasEffect(MobEffects.JUMP)) {
      if (!this.stepIncreased) {
        this.setMaxUpStep(1.0F);
        this.stepIncreased = true;
      }
    } else if (this.stepIncreased) {
      this.setMaxUpStep(this.baseUpStep);
      this.stepIncreased = false;
    }
  }

  @ModifyVariable(
      method = "travel(" + "Lnet/minecraft/world/phys/Vec3;" + ")V",
      require = 1,
      allow = 1,
      at =
          @At(
              shift = At.Shift.BEFORE,
              value = "INVOKE",
              opcode = Opcodes.INVOKEVIRTUAL,
              ordinal = 0,
              target = "Lnet/minecraft/world/entity/LivingEntity;" + "resetFallDistance(" + ")V"))
  private double dropIfCrouching(final double fallDelta) {
    return this.isCrouching() ? 0.08 : fallDelta;
  }
}
