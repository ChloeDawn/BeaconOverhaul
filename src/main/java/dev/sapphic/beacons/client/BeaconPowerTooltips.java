package dev.sapphic.beacons.client;

import dev.sapphic.beacons.BeaconTier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

@Environment(EnvType.CLIENT)
public final class BeaconPowerTooltips {
  private static final String[] EFFECT_SUFFIXES = { " II", " III", " IV" };

  private BeaconPowerTooltips() {
  }

  public static Component createTooltip(final BeaconScreen screen, final MobEffect effect, final boolean primary) {
    final TranslatableComponent component = new TranslatableComponent(effect.getDescriptionId());

    if (effect != MobEffects.SLOW_FALLING) {
      boolean additional = !primary;

      if (additional) {
        for (final MobEffect e : BeaconBlockEntity.BEACON_EFFECTS[3]) {
          if (effect.equals(e)) {
            additional = false;
            break;
          }
        }
      }

      int index = additional ? 1 : 0;

      if (effect != MobEffects.NIGHT_VISION) {
        index += BeaconTier.get(screen.getMenu()).ordinal();
      }

      if (index > 0) {
        return component.append(EFFECT_SUFFIXES[index - 1]);
      }
    }

    return component;
  }
}
