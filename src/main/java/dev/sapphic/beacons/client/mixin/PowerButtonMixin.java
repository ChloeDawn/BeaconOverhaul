package dev.sapphic.beacons.client.mixin;

import dev.sapphic.beacons.client.BeaconPowerTooltips;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.world.effect.MobEffect;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BeaconScreen.BeaconPowerButton.class)
abstract class PowerButtonMixin extends BeaconScreen.BeaconScreenButton {
  @Unique
  @SuppressWarnings("ConstantConditions")
  private final boolean upgrade = (Object) this instanceof BeaconScreen.BeaconUpgradePowerButton;

  @Shadow(aliases = "this$0")
  @Final
  private @MonotonicNonNull BeaconScreen this$0;

  @Shadow
  private @MonotonicNonNull MobEffect effect;

  PowerButtonMixin(final int x, final int y) {
    super(x, y);
  }

  private void setTieredTooltip(final MobEffect effect) {
    this.setTooltip(Tooltip.create(BeaconPowerTooltips.createTooltip(this.this$0, effect, this.upgrade), null));
  }

  @Inject(method = "setEffect(Lnet/minecraft/world/effect/MobEffect;)V", at = @At("RETURN"), require = 1, allow = 1)
  private void setTieredTooltip(final MobEffect effect, final CallbackInfo ci) {
    this.setTieredTooltip(this.effect);
  }

  @Inject(method = "updateStatus(I)V", at = @At("TAIL"), require = 1, allow = 1)
  private void updateTieredTooltip(final CallbackInfo ci) {
    if (!this.upgrade) {
      this.setTieredTooltip(this.effect);
    }
  }
}
