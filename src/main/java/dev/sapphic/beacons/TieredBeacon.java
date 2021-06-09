package dev.sapphic.beacons;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public interface TieredBeacon {
  PotencyTier getTier();

  static int updateBaseAndTier(
    final BeaconBlockEntity beacon, final Level level, final int x, final int y, final int z
  ) {
    var levels = 0;
    var maxTier = PotencyTier.HIGH;
    var layer = 1;
    while (layer <= 4) {
      final var oy = y - layer;
      if (oy < level.getMinBuildHeight()) {
        break;
      }
      var valid = true;
      for(var ox = x - layer; (ox <= (x + layer)) && valid; ++ox) {
        for(var oz = z - layer; oz <= (z + layer); ++oz) {
          final var state = level.getBlockState(new BlockPos(ox, oy, oz));
          if (!state.is(BlockTags.BEACON_BASE_BLOCKS)) {
            valid = false;
            break;
          }
          final var tier = PotencyTier.maxOf(state);
          if (tier.ordinal() < maxTier.ordinal()) {
            maxTier = tier;
          }
        }
      }
      if (!valid) {
        return levels;
      }
      levels = layer;
      layer++;
    }
    PotencyTier.set(beacon, maxTier);
    return levels;
  }

  static void applyTieredEffects(
    final BeaconBlockEntity beacon, final Level level, final BlockPos pos, final int levels,
    final @Nullable MobEffect primary, final @Nullable MobEffect secondary
  ) {
    if (level.isClientSide || (primary == null)) {
      return;
    }

    final var tier = PotencyTier.get(beacon).ordinal();
    var primaryAmplifier = tier;
    var secondaryAmplifier = tier;

    if ((primary == MobEffects.NIGHT_VISION) || (primary == MobEffects.FIRE_RESISTANCE)) {
      primaryAmplifier = 0;
    }

    if (secondary == MobEffects.SLOW_FALLING) {
      secondaryAmplifier = 0;
    }

    if ((levels >= 4) && (primary == secondary) && (primary != MobEffects.FIRE_RESISTANCE)) {
      primaryAmplifier = Math.min(primaryAmplifier, secondaryAmplifier);
      primaryAmplifier++;
    }

    final var radius = (levels * 10.0) + (10.0 * (tier + 1));
    final var duration = ((9 * (tier + 1)) + (levels * 2)) * 20;
    final var range = new AABB(pos).inflate(radius).expandTowards(0.0, level.getHeight(), 0.0);
    final var uniqueSecondary = (levels >= 4) && (primary != secondary) && (secondary != null);

    for (final var player : level.getEntitiesOfClass(Player.class, range)) {
      player.addEffect(new MobEffectInstance(primary, duration, primaryAmplifier, true, true));

      if (uniqueSecondary) {
        player.addEffect(new MobEffectInstance(secondary, duration, secondaryAmplifier, true, true));
      }
    }
  }
}
