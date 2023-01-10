package dev.sapphic.beacons;

import com.google.common.collect.ObjectArrays;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.sapphic.beacons.mixin.BeaconBlockEntityAccessor;
import dev.sapphic.beacons.mixin.GameRulesAccessor;
import dev.sapphic.beacons.mixin.IntegerValueAccessor;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class BeaconMobEffects implements ModInitializer {
  public static final GameRules.Key<GameRules.IntegerValue> LONG_REACH_INCREMENT =
      GameRulesAccessor.callRegister(
          "longReachIncrement", GameRules.Category.PLAYER, IntegerValueAccessor.callCreate(2));

  public static final MobEffect LONG_REACH =
      new MobEffect(MobEffectCategory.BENEFICIAL, 0xDEF58F) {
        private static double getLongReachAmount(final LivingEntity entity, final int mul) {
          return Math.max(0, entity.level.getGameRules().getInt(LONG_REACH_INCREMENT)) * (mul + 1);
        }

        @Override
        public void addAttributeModifiers(
            final LivingEntity entity, final AttributeMap attributes, final int mul) {
          for (final var entry : this.getAttributeModifiers().entrySet()) {
            final @Nullable AttributeInstance instance = attributes.getInstance(entry.getKey());

            if (instance != null) {
              final var modifier = entry.getValue();

              instance.removeModifier(modifier);
              instance.addPermanentModifier(
                  new AttributeModifier(
                      modifier.getId(),
                      this.getDescriptionId() + ' ' + mul,
                      getLongReachAmount(entity, mul),
                      modifier.getOperation()));
            }
          }
        }
      }.addAttributeModifier(
              ReachEntityAttributes.ATTACK_RANGE,
              "C764C44F-FC32-498B-98EB-B3262BA58B3B",
              Double.NaN,
              Operation.ADDITION)
          .addAttributeModifier(
              ReachEntityAttributes.REACH,
              "C20A0A8F-83DF-4C37-BC34-3678C24C3F01",
              Double.NaN,
              Operation.ADDITION);

  public static final MobEffect NUTRITION =
      new MobEffect(MobEffectCategory.BENEFICIAL, 0xC75F79) {
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

  static {
    addMobEffectsToBeacon();
  }

  private static void addMobEffectsToBeacon() {
    final var effects = BeaconBlockEntity.BEACON_EFFECTS;

    effects[0] = ObjectArrays.concat(effects[0], MobEffects.NIGHT_VISION);
    effects[1] = ObjectArrays.concat(effects[1], LONG_REACH);
    effects[2] = ObjectArrays.concat(effects[2], NUTRITION);
    effects[3] = ObjectArrays.concat(effects[3], MobEffects.FIRE_RESISTANCE);
    effects[3] = ObjectArrays.concat(effects[3], MobEffects.SLOW_FALLING);

    BeaconBlockEntityAccessor.setValidEffects(
        Arrays.stream(effects).flatMap(Arrays::stream).collect(Collectors.toSet()));
  }

  private static void registerMobEffect(final String name, final MobEffect effect) {
    Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(NAMESPACE, name), effect);
  }

  @Override
  public void onInitialize() {
    registerMobEffect("long_reach", LONG_REACH);
    registerMobEffect("nutrition", NUTRITION);
  }
}
