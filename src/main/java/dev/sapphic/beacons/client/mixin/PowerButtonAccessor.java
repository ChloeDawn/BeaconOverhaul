package dev.sapphic.beacons.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// TODO See https://github.com/FabricMC/fabric-loom/issues/311
// FIXME Mixin reporting package-private class target as public

@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.gui.screens.inventory.BeaconScreen$BeaconPowerButton")
public interface PowerButtonAccessor {
	@Accessor
	MobEffect getEffect();
}
