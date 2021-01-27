package dev.sapphic.beacons.mixin.reach;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = {
  AbstractFurnaceBlockEntity.class,
  AbstractMinecartContainer.class,
  BrewingStandBlockEntity.class,
  Inventory.class,
  RandomizableContainerBlockEntity.class
}, targets = {
  "net.minecraft.world.level.block.entity.LecternBlockEntity$1"
})
abstract class ContainersMixin implements Container {
  @ModifyConstant(
    method = "stillValid(Lnet/minecraft/world/entity/player/Player;)Z",
    require = 1, allow = 1,
    constant = @Constant(doubleValue = 64.0D))
  private static double modifyReachDistance(final double value, final Player player) {
    return ReachEntityAttributes.getSquaredReachDistance(player, value);
  }
}
