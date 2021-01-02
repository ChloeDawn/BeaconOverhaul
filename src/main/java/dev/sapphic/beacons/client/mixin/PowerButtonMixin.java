package dev.sapphic.beacons.client.mixin;

import dev.sapphic.beacons.client.BeaconPowerTooltips;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

// TODO Compile-time access widener for target, superclass, and parameter
//  Compile-time access wideners are currently unsupported
//  See https://github.com/FabricMC/fabric-loom/issues/311
//  Also report Mixin bug of reporting package-private class target as public
//  It is possibly a conflict with Fabric Loader making classes public at runtime?

@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.gui.screens.inventory.BeaconScreen$BeaconPowerButton")
abstract class PowerButtonMixin extends AbstractButton /*extends BeaconScreen.BeaconScreenButton*/ {
  PowerButtonMixin(final int x, final int y, final int w, final int h, final Component label) {
    super(x, y, w, h, label);
  }

  // FIXME Report plugin issues; it neither recognizes inner class constructor signatures nor type coercion
  @SuppressWarnings({ "UnnecessaryQualifiedMemberReference", "UnresolvedMixinReference" })
  @Redirect(
    method = "Lnet/minecraft/client/gui/screens/inventory/BeaconScreen$BeaconPowerButton;<init>(Lnet/minecraft/client/gui/screens/inventory/BeaconScreen;IILnet/minecraft/world/effect/MobEffect;Z)V",
    at = @At(value = "INVOKE",
      target = "Lnet/minecraft/client/gui/screens/inventory/BeaconScreen$BeaconPowerButton;createTooltip(Lnet/minecraft/world/effect/MobEffect;Z)Lnet/minecraft/network/chat/Component;"),
    require = 1, allow = 1)
  private Component createTieredTooltip(
    @Coerce final AbstractButton button, final MobEffect effect, final boolean primary,
    // Enclosing method parameters
    final BeaconScreen screen, final int x, final int y, final MobEffect effect1, final boolean primary1
  ) {
    return BeaconPowerTooltips.createTooltip(screen, effect, primary);
  }
}
