package dev.sapphic.beacons.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MobEffect.class)
public interface MobEffectAccessor {
  // TODO Omit value when Mixin constructor invoker refmap issue is resolved
  //  https://github.com/SpongePowered/Mixin/issues/454
  @Invoker("<init>")
  static MobEffect newMobEffect(final MobEffectCategory category, final int color) {
    throw new AssertionError();
  }
}
