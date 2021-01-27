package dev.sapphic.beacons.mixin.reach;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemCombinerMenu.class)
abstract class ItemCombinerMenuMixin extends AbstractContainerMenu {
  ItemCombinerMenuMixin(final MenuType<?> type, final int syncId) {
    super(type, syncId);
  }

  @ModifyConstant(
    method = "lambda$stillValid$1(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Ljava/lang/Boolean;",
    require = 1, allow = 1,
    constant = @Constant(doubleValue = 64.0D))
  private double modifyReachDistance(final double value, final Player player) {
    return ReachEntityAttributes.getSquaredReachDistance(player, value);
  }
}
