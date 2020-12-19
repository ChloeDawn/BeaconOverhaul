package dev.sapphic.beacons.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
abstract class SlowFallingMixin extends Entity {
  SlowFallingMixin(final EntityType<?> type, final Level level) {
    super(type, level);
  }

  @Shadow
  public abstract boolean hasEffect(final MobEffect mobEffect);

  @ModifyConstant(method = "travel", constant = @Constant(doubleValue = 0.01, ordinal = 0), require = 1, allow = 1)
  private double dropIfCrouching(final double fallDelta) {
    return (this.hasEffect(MobEffects.SLOW_FALLING) && !this.isCrouching()) ? fallDelta : 0.08;
  }
}
