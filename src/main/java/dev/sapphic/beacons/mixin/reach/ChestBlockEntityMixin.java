package dev.sapphic.beacons.mixin.reach;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlockEntity.class)
abstract class ChestBlockEntityMixin {
  @Inject(
    method = "getOpenCount(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/BaseContainerBlockEntity;III)I",
    at = @At("HEAD"), cancellable = true
  )
  private static void getReachAccountingOpenCount(final Level level, final BaseContainerBlockEntity be, final int x, final int y, final int z, final CallbackInfoReturnable<Integer> info) {
    int count = 0;
    // Maximum reach distance applicable from beacons. Unsure how to generalize this for upstream library
    final double maxRange = 5.0 + (5.0 * 3);
    final AABB aabb = new AABB(x - maxRange, y - maxRange, z - maxRange, x + maxRange, y + maxRange, z + maxRange);
    for (final Player player : level.getEntitiesOfClass(Player.class, aabb)) {
      if (player.containerMenu instanceof ChestMenu) {
        final Container cr = ((ChestMenu) player.containerMenu).getContainer();
        if ((cr == be) || ((cr instanceof CompoundContainer) && ((CompoundContainer) cr).contains(be))) {
          final double reach = ReachEntityAttributes.getReachDistance(player, 5.0);
          final AABB testAabb = new AABB(x - reach, y - reach, z - reach, x + reach, y + reach, z + reach);
          if (player.getBoundingBox().intersects(testAabb)) {
            ++count;
          }
        }
      }
    }
    info.setReturnValue(count);
  }
}
