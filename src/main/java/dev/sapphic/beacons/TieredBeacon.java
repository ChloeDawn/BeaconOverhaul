package dev.sapphic.beacons;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public interface TieredBeacon {
  BeaconTier getTier();

  static int updateBaseAndTier(
    final BeaconBlockEntity beacon, final Level level, final int x, final int y, final int z
  ) {
    int levels = 0;
    boolean netherite = true;
    boolean diamond = true;
    int layer = 1;
    while (layer <= 4) {
      final int oy = y - layer;
      if (oy < 0 /*Min build height*/) {
        break;
      }
      boolean valid = true;
      for(int ox = x - layer; (ox <= (x + layer)) && valid; ++ox) {
        for(int oz = z - layer; oz <= (z + layer); ++oz) {
          final BlockState state = level.getBlockState(new BlockPos(ox, oy, oz));
          if (!state.is(BlockTags.BEACON_BASE_BLOCKS)) {
            valid = false;
            break;
          }
          if (!state.is(Blocks.NETHERITE_BLOCK)) {
            netherite = false;
          }
          if (!state.is(Blocks.DIAMOND_BLOCK)) {
            diamond = false;
          }
        }
      }
      if (!valid) {
        return levels;
      }
      levels = layer;
      layer++;
    }
    BeaconTier.set(beacon, BeaconTier.of(diamond, netherite));
    return levels;
  }

  static void applyTieredEffects(
    final BeaconBlockEntity beacon, final Level level, final BlockPos pos, final int levels,
    final @Nullable MobEffect primary, final @Nullable MobEffect secondary
  ) {
    if (level.isClientSide || (primary == null)) {
      return;
    }

    final int tier = BeaconTier.get(beacon).ordinal();
    int primaryAmplifier = tier;
    int secondaryAmplifier = tier;

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

    final double radius = (levels * 10) + (10 * (tier + 1));
    final int duration = ((9 * (tier + 1)) + (levels * 2)) * 20;
    final AABB range = new AABB(pos).inflate(radius).expandTowards(0.0, level.getHeight(), 0.0);
    final boolean uniqueSecondary = (levels >= 4) && (primary != secondary) && (secondary != null);

    for (final Player player : level.getEntitiesOfClass(Player.class, range)) {
      player.addEffect(new MobEffectInstance(primary, duration, primaryAmplifier, true, true));

      if (uniqueSecondary) {
        player.addEffect(new MobEffectInstance(secondary, duration, secondaryAmplifier, true, true));
      }
    }
  }
}
