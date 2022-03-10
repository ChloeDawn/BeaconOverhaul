package dev.sapphic.beacons.mixin;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.class)
public interface GameRulesAccessor {
  @Invoker
  static <T extends GameRules.Value<T>> GameRules.Key<T> callRegister(
      final String name, final GameRules.Category category, final GameRules.Type<T> type) {
    throw new AssertionError();
  }
}
