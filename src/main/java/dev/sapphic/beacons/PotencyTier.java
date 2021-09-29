package dev.sapphic.beacons;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public enum PotencyTier {
  NONE, LOW, HIGH;

  private static final Tag<Block> LOW_POTENCY_BLOCKS = blocks("low_potency");
  private static final Tag<Block> HIGH_POTENCY_BLOCKS = blocks("high_potency");

  private static final PotencyTier[] TIERS = values();

  public static PotencyTier valueOf(final int ordinal) {
    return TIERS[ordinal % TIERS.length];
  }

  public static PotencyTier get(final Object o) {
    return ((TieredBeacon) o).getTier();
  }

  public static void set(final Object o, final PotencyTier tier) {
    ((MutableTieredBeacon) o).setTier(tier);
  }

  static PotencyTier maxOf(final BlockState state) {
    if (!state.is(HIGH_POTENCY_BLOCKS)) {
      return state.is(LOW_POTENCY_BLOCKS) ? LOW : NONE;
    }

    return HIGH;
  }

  private static Tag<Block> blocks(final String name) {
    return TagFactory.BLOCK.create(new ResourceLocation(BeaconMobEffects.NAMESPACE, name));
  }
}
