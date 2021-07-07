package dev.sapphic.beacons.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(BeaconScreen.BeaconPowerButton.class)
public interface PowerButtonAccessor {
	@Accessor
	MobEffect getEffect();
}
