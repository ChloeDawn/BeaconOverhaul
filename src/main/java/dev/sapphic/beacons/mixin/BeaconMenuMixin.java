package dev.sapphic.beacons.mixin;

import dev.sapphic.beacons.PotencyTier;
import dev.sapphic.beacons.TieredBeacon;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BeaconMenu.class)
abstract class BeaconMenuMixin extends AbstractContainerMenu implements TieredBeacon {
  @Shadow @Final private ContainerData beaconData;

  BeaconMenuMixin(final @Nullable MenuType<?> type, final int id) {
    super(type, id);
  }

  @Override
  public final PotencyTier getTier() {
    return PotencyTier.valueOf(this.beaconData.get(3));
  }

  @ModifyConstant(method = "<init>(ILnet/minecraft/world/Container;)V",
    require = 1, allow = 1,
    constant = @Constant(intValue = 3))
  private static int getNewDataCount(final int dataCount) {
    return 4;
  }

  @ModifyConstant(
    method = "<init>(ILnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
    require = 1, allow = 1,
    constant = @Constant(intValue = 3, ordinal = 0))
  private int getDataPreconditionCount(final int dataCount) {
    return 4;
  }
}
