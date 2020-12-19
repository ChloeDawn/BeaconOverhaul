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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
  private /*final*/ float defaultMaxStepUp;

  LivingEntityMixin(final EntityType<?> type, final Level level) {
    super(type, level);
  }

  @Inject(method = "<init>", at = @At("RETURN"), require = 1, allow = 1)
  private void storeDefaultMaxStepUp(final CallbackInfo ci) {
    this.defaultMaxStepUp = this.maxUpStep;
  }

  @Shadow
  public abstract boolean hasEffect(final MobEffect effect);

  @Inject(method = "tickEffects", at = @At("HEAD"), require = 1, allow = 1)
  private void updateJumpBoostStepAssist(final CallbackInfo ci) {
    this.maxUpStep = this.hasEffect(MobEffects.JUMP) ? 1.0F : this.defaultMaxStepUp;
  }
}
