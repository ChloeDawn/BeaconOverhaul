package dev.sapphic.beacons.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
  @Accessor("VALID_EFFECTS")
  static void setValidEffects(final Set<MobEffect> value) {
    throw new AssertionError();
  }
}
