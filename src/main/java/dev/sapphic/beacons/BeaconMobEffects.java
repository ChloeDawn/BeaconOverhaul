package dev.sapphic.beacons;

import com.google.common.collect.ObjectArrays;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.sapphic.beacons.mixin.BeaconBlockEntityAccessor;
import dev.sapphic.beacons.mixin.MobEffectAccessor;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class BeaconMobEffects implements ModInitializer {
  public static final MobEffect LONG_REACH = MobEffectAccessor.newMobEffect(MobEffectCategory.BENEFICIAL, 0xDEF58F)
    .addAttributeModifier(ReachEntityAttributes.REACH, "C20A0A8F-83DF-4C37-BC34-3678C24C3F01", 5.0, Operation.ADDITION)
    .addAttributeModifier(ReachEntityAttributes.ATTACK_RANGE, "C764C44F-FC32-498B-98EB-B3262BA58B3B", 5.0, Operation.ADDITION);

  public static final MobEffect NUTRITION = new MobEffect(MobEffectCategory.BENEFICIAL, 0xC75F79) {
    @Override
    public void applyEffectTick(final LivingEntity entity, final int amplifier) {
      if (entity instanceof Player) {
        ((Player) entity).getFoodData().eat(1, 0.0F);
      }
    }

    @Override
    public boolean isDurationEffectTick(final int duration, final int amplifier) {
      return ((100 >> amplifier) <= 0) || ((duration % (100 >> amplifier)) == 0);
    }
  };

  static final String NAMESPACE = "beaconoverhaul";

  public BeaconMobEffects() {
    appendAdditionalEffects();
  }

  private static void appendAdditionalEffects() {
    final var effects = BeaconBlockEntity.BEACON_EFFECTS;

    effects[0] = ObjectArrays.concat(effects[0], MobEffects.NIGHT_VISION);
    effects[1] = ObjectArrays.concat(effects[1], LONG_REACH);
    effects[2] = ObjectArrays.concat(effects[2], NUTRITION);
    effects[3] = ObjectArrays.concat(effects[3], MobEffects.FIRE_RESISTANCE);
    effects[3] = ObjectArrays.concat(effects[3], MobEffects.SLOW_FALLING);

    BeaconBlockEntityAccessor.setValidEffects(Arrays.stream(effects)
      .flatMap(Arrays::stream).collect(Collectors.toSet()));
  }

  @Override
  public void onInitialize() {
    Registry.register(Registry.MOB_EFFECT, new ResourceLocation(NAMESPACE, "long_reach"), LONG_REACH);
    Registry.register(Registry.MOB_EFFECT, new ResourceLocation(NAMESPACE, "nutrition"), NUTRITION);
  }
}
