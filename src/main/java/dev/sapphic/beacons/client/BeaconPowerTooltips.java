package dev.sapphic.beacons.client;

import dev.sapphic.beacons.TieredBeacon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

@Environment(EnvType.CLIENT)
public final class BeaconPowerTooltips {
  private static final String[] EFFECT_SUFFIXES = { " II", " III", " IV" };

  private BeaconPowerTooltips() {
  }

  public static MutableComponent createTooltip(
      final BeaconScreen screen, final MobEffect effect, final boolean upgrade) {
    final var component = Component.translatable(effect.getDescriptionId());

    if ((effect != MobEffects.SLOW_FALLING) && (effect != MobEffects.FIRE_RESISTANCE)) {
      var potency = upgrade ? 1 : 0;

      if (effect != MobEffects.NIGHT_VISION) {
        potency += ((TieredBeacon) screen.getMenu()).getTier().ordinal();
      }

      if (potency > 0) {
        return component.append(EFFECT_SUFFIXES[potency - 1]);
      }
    }

    return component;
  }
}
