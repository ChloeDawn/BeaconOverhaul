package dev.sapphic.beacons.mixin.reach;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EnderChestBlockEntity.class)
abstract class EnderChestBlockEntityMixin extends BlockEntity {
  EnderChestBlockEntityMixin(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
    super(type, pos, state);
  }

  @ModifyConstant(
    method = "stillValid(Lnet/minecraft/world/entity/player/Player;)Z",
    require = 1, allow = 1,
    constant = @Constant(doubleValue = 64.0D))
  private static double modifyReachDistance(final double value, final Player player) {
    return ReachEntityAttributes.getSquaredReachDistance(player, value);
  }
}
