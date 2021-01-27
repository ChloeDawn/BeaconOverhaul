package dev.sapphic.beacons.mixin.reach;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
  @ModifyConstant(
    method = "lambda$stillValid$0(Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Ljava/lang/Boolean;",
    require = 1, allow = 1,
    constant = @Constant(doubleValue = 64.0D))
  private static double modifyReachDistance(final double value, final Block block, final Player player) {
    return ReachEntityAttributes.getSquaredReachDistance(player, value);
  }
}
