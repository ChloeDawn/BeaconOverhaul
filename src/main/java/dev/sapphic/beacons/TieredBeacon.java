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

  void setTier(final BeaconTier tier);

  static int updateBaseAndTier(
    final BeaconBlockEntity beacon, final Level level, final int x, final int y, final int z
  ) {
    int oy;
    int levels = 0;
    int layers = 1;
    boolean diamond = true;
    boolean netherite = true;
    //noinspection NestedAssignment TODO This needs rewriting, it's a mess
    while ((layers <= 4) && ((oy = y - layers) >= level.getMinBuildHeight())) {
      boolean valid = true;
      layerCheck:
      for (int ox = x - layers; (ox <= (x + layers)) && valid; ++ox) {
        for (int oz = z - layers; oz <= (z + layers); ++oz) {
          final BlockState state = level.getBlockState(new BlockPos(ox, oy, oz));
          if (state.is(BlockTags.BEACON_BASE_BLOCKS)) {
            if (state.getBlock() != Blocks.DIAMOND_BLOCK) {
              diamond = false;
              if (state.getBlock() != Blocks.NETHERITE_BLOCK) {
                netherite = false;
              }
            }
            continue;
          }
          valid = false;
          continue layerCheck;
        }
      }
      if (!valid) {
        return levels;
      }
      levels = layers;
      layers++;
    }
    //noinspection CastToIncompatibleInterface
    ((TieredBeacon) beacon).setTier(BeaconTier.of(diamond, netherite));
    return levels;
  }

  static void applyTieredEffects(
    final BeaconBlockEntity beacon, final Level level, final BlockPos pos, final int levels,
    final @Nullable MobEffect primary, final @Nullable MobEffect secondary
  ) {
    if (level.isClientSide || (primary == null)) {
      return;
    }
    //noinspection CastToIncompatibleInterface
    final int tier = ((TieredBeacon) beacon).getTier().ordinal();
    int primaryAmplifier = tier;
    int secondaryAmplifier = tier;
    if (primary == MobEffects.NIGHT_VISION) {
      primaryAmplifier = 0;
    }
    if (secondary == MobEffects.SLOW_FALLING) {
      secondaryAmplifier = 0;
    }
    if ((levels >= 4) && (primary == secondary)) {
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
