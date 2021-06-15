package dev.sapphic.beacons;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.common.collect.ObjectArrays;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.sapphic.beacons.mixin.BeaconBlockEntityAccessor;
import dev.sapphic.beacons.mixin.MobEffectAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
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

  public static final String NAMESPACE = "beaconoverhaul";

  @Override
  public void onInitialize() {
    Registry.register(Registry.MOB_EFFECT, new ResourceLocation(NAMESPACE, "long_reach"), LONG_REACH);
    Registry.register(Registry.MOB_EFFECT, new ResourceLocation(NAMESPACE, "nutrition"), NUTRITION);

    final var configs = FabricLoader.getInstance().getConfigDir();
    final var config = FileConfig.of(configs.resolve(NAMESPACE + ".toml"));
    final var spec = new ConfigSpec();


    spec.define("effects.night_vision", Boolean.TRUE);
    spec.define("effects.long_reach", Boolean.TRUE);
    spec.define("effects.nutrition", Boolean.TRUE);
    spec.define("effects.fire_resistance", Boolean.TRUE);
    spec.define("effects.slow_falling", Boolean.TRUE);

    config.load();
    spec.correct(config);
    config.save();

    final var effects = BeaconBlockEntity.BEACON_EFFECTS;

    if (config.getOrElse("effects.night_vision", Boolean.TRUE)) {
      effects[0] = ObjectArrays.concat(effects[0], MobEffects.NIGHT_VISION);
    }

    if (config.getOrElse("effects.long_reach", Boolean.TRUE)) {
      effects[1] = ObjectArrays.concat(effects[1], LONG_REACH);
    }

    if (config.getOrElse("effects.nutrition", Boolean.TRUE)) {
      effects[2] = ObjectArrays.concat(effects[2], NUTRITION);
    }

    if (config.getOrElse("effects.fire_resistance", Boolean.TRUE)) {
      effects[3] = ObjectArrays.concat(effects[3], MobEffects.FIRE_RESISTANCE);
    }

    if (config.getOrElse("effects.slow_falling", Boolean.TRUE)) {
      effects[3] = ObjectArrays.concat(effects[3], MobEffects.SLOW_FALLING);
    }

    BeaconBlockEntityAccessor.setValidEffects(Arrays.stream(effects)
      .flatMap(Arrays::stream).collect(Collectors.toSet()));
  }
}
