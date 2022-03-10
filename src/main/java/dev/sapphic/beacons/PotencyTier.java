package dev.sapphic.beacons;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum PotencyTier {
  NONE,
  LOW,
  HIGH;

  public static final TagKey<Block> LOW_POTENCY_BLOCKS = createBlockTag("low_potency");
  public static final TagKey<Block> HIGH_POTENCY_BLOCKS = createBlockTag("high_potency");

  private static TagKey<Block> createBlockTag(final String name) {
    final var id = new ResourceLocation(BeaconMobEffects.NAMESPACE, name);

    return TagKey.create(Registry.BLOCK_REGISTRY, id);
  }
}
