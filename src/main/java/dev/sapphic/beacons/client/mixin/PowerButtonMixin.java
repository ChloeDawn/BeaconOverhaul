package dev.sapphic.beacons.client.mixin;

import dev.sapphic.beacons.client.BeaconPowerTooltips;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

// TODO See https://github.com/FabricMC/fabric-loom/issues/311
// FIXME Mixin reporting package-private class target as public

@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.gui.screens.inventory.BeaconScreen$BeaconPowerButton")
abstract class PowerButtonMixin extends AbstractButton /*extends BeaconScreen.BeaconScreenButton*/ {
  PowerButtonMixin(final int x, final int y, final int w, final int h, final Component label) {
    super(x, y, w, h, label);
  }

  // FIXME Plugin does not recognize inner class descriptors
  @SuppressWarnings("UnresolvedMixinReference")
  @Redirect(
    method = "<init>(Lnet/minecraft/client/gui/screens/inventory/BeaconScreen;IILnet/minecraft/world/effect/MobEffect;Z)V",
    require = 1, allow = 1,
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lnet/minecraft/client/gui/screens/inventory/BeaconScreen$BeaconPowerButton;createTooltip(Lnet/minecraft/world/effect/MobEffect;Z)Lnet/minecraft/network/chat/Component;"))
  private Component createTieredTooltip(
    @Coerce final AbstractButton button, final MobEffect effect, final boolean primary,
    // Enclosing method parameters
    final BeaconScreen screen, final int x, final int y, final MobEffect effect1, final boolean primary1
  ) {
    return BeaconPowerTooltips.createTooltip(screen, effect, primary);
  }
}
