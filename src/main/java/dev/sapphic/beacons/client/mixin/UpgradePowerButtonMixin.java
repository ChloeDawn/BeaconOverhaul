package dev.sapphic.beacons.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BeaconScreen.BeaconUpgradePowerButton.class)
abstract class UpgradePowerButtonMixin extends BeaconScreen.BeaconScreenButton {
  @Shadow(aliases = "this$0") @Final BeaconScreen this$0;

  protected UpgradePowerButtonMixin(final int x, final int y) {
    super(x, y);
  }

  @Inject(method = "updateStatus(I)V",
    require = 1, allow = 1,
    at = @At("HEAD"),
    cancellable = true)
  private void hideSecondaryFireResistance(final int tier, final CallbackInfo ci) {
    //noinspection CastToIncompatibleInterface
    if (((BeaconScreenAccessor) this.this$0).getPrimary() == MobEffects.FIRE_RESISTANCE) {
      this.visible = false;
      ci.cancel();
    }
  }
}
