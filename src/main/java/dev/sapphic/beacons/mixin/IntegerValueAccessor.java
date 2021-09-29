package dev.sapphic.beacons.mixin;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.IntegerValue.class)
public interface IntegerValueAccessor {
  @Invoker
  static GameRules.Type<GameRules.IntegerValue> callCreate(final int defaultValue) {
    throw new AssertionError();
  }
}
