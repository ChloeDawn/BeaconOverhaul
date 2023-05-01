package dev.sapphic.beacons.client.mixin;

import dev.sapphic.beacons.client.BeaconPowerTooltips;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BeaconScreen.BeaconPowerButton.class)
abstract class PowerButtonMixin extends BeaconScreen.BeaconScreenButton {
  @Final
  @Shadow(aliases = "this$0")
  private BeaconScreen this$0;

  @Shadow
  private MobEffect effect;

  PowerButtonMixin(final int x, final int y) {
    super(x, y);
  }

  @Unique
  @SuppressWarnings("ConstantConditions")
  private boolean isUpgradeButton() {
    return (Object) this instanceof BeaconScreen.BeaconUpgradePowerButton;
  }

  @Redirect(
      method = "setEffect(" + "Lnet/minecraft/world/effect/MobEffect;" + ")V",
      require = 1,
      allow = 1,
      at =
          @At(
              value = "INVOKE",
              opcode = Opcodes.INVOKEVIRTUAL,
              target =
                  "Lnet/minecraft/client/gui/screens/inventory/BeaconScreen$BeaconPowerButton;"
                      + "createEffectDescription("
                      + "Lnet/minecraft/world/effect/MobEffect;"
                      + ")Lnet/minecraft/network/chat/MutableComponent;"))
  private MutableComponent createTieredTooltip(
      final BeaconScreen.BeaconPowerButton button, final MobEffect effect) {
    return BeaconPowerTooltips.createTooltip(this.this$0, effect, this.isUpgradeButton());
  }

  @Inject(method = "updateStatus(" + "I" + ")V", require = 1, allow = 1, at = @At("TAIL"))
  private void updateTooltip(final int levels, final CallbackInfo ci) {
    if (!this.isUpgradeButton()) {
      this.setTooltip(Tooltip.create(this.createTieredTooltip((BeaconScreen.BeaconPowerButton) (Object) this, this.effect), null));
    }
  }
}
