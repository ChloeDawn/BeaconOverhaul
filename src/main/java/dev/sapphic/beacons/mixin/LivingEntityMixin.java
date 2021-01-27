package dev.sapphic.beacons.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
  private float defaultMaxUpStep = Float.NaN;

  LivingEntityMixin(final EntityType<?> type, final Level level) {
    super(type, level);
  }

  @Shadow
  public abstract boolean hasEffect(final MobEffect effect);

  @Inject(method = "tickEffects()V",
    require = 1, allow = 1,
    at = @At("HEAD"))
  private void updateJumpBoostStepAssist(final CallbackInfo ci) {
    if (Float.isNaN(this.defaultMaxUpStep)) {
      this.defaultMaxUpStep = this.maxUpStep;
      if (Float.isNaN(this.defaultMaxUpStep)) {
        throw new IllegalStateException("Max up step is not a number");
      }
    }
    this.maxUpStep = this.hasEffect(MobEffects.JUMP) ? 1.0F : this.defaultMaxUpStep;
  }

  @ModifyConstant(method = "travel(Lnet/minecraft/world/phys/Vec3;)V",
    require = 1, allow = 1,
    constant = @Constant(doubleValue = 0.01, ordinal = 0))
  private double dropIfCrouching(final double fallDelta) {
    return (this.hasEffect(MobEffects.SLOW_FALLING) && !this.isCrouching()) ? fallDelta : 0.08;
  }
}
