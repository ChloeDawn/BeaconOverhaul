package dev.sapphic.beacons.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BeaconMenu;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(BeaconScreen.class)
abstract class BeaconScreenMixin extends AbstractContainerScreen<BeaconMenu> {
	BeaconScreenMixin(final BeaconMenu menu, final Inventory inventory, final Component component) {
		super(menu, inventory, component);
	}

	@Redirect(method = "tick()V",
		require = 1, allow = 1,
		at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL, ordinal = 2,
			target = "Lnet/minecraft/client/gui/screens/inventory/BeaconScreen;addButton(Lnet/minecraft/client/gui/components/AbstractWidget;)Lnet/minecraft/client/gui/components/AbstractWidget;"))
	private <T extends AbstractWidget> T hideFireResistant(final BeaconScreen beaconScreen, final T button) {
		//noinspection CastToIncompatibleInterface
		return (((PowerButtonAccessor) button).getEffect() == MobEffects.FIRE_RESISTANCE) ? button : this.addButton(button);
	}
}
