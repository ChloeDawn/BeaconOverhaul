package dev.sapphic.beacons;

import com.google.common.collect.ObjectArrays;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.sapphic.beacons.mixin.MobEffectAccessor;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public final class BeaconMobEffects implements ModInitializer {
  public static final MobEffect LONG_REACH = MobEffectAccessor.newMobEffect(MobEffectCategory.BENEFICIAL, 0xDEF58F)
    .addAttributeModifier(ReachEntityAttributes.REACH, "C20A0A8F-83DF-4C37-BC34-3678C24C3F01", 5.0, Operation.ADDITION)
    .addAttributeModifier(ReachEntityAttributes.ATTACK_RANGE, "C764C44F-FC32-498B-98EB-B3262BA58B3B", 5.0, Operation.ADDITION);

  public static void appendAdditionalEffects(final MobEffect[][] effects) {
    effects[0] = ObjectArrays.concat(effects[0], MobEffects.NIGHT_VISION);
    effects[1] = ObjectArrays.concat(effects[1], MobEffects.FIRE_RESISTANCE);
    effects[3] = concat(effects[3], LONG_REACH, MobEffects.SLOW_FALLING);
  }

  private static MobEffect[] concat(final MobEffect[] first, final MobEffect... second) {
    return ObjectArrays.concat(first, second, MobEffect.class);
  }

  @Override
  public void onInitialize() {
    Registry.register(Registry.MOB_EFFECT, new ResourceLocation("beaconoverhaul", "long_reach"), LONG_REACH);
  }
}
