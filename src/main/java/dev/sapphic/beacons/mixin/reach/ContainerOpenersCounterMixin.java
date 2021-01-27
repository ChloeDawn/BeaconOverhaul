package dev.sapphic.beacons.mixin.reach;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.phys.AABB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ContainerOpenersCounter.class)
abstract class ContainerOpenersCounterMixin {
  @Shadow
  protected abstract boolean isOwnContainer(final Player player);

  @Redirect(
    method = "recheckOpeners(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
    require = 1, allow = 1,
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lnet/minecraft/world/level/block/entity/ContainerOpenersCounter;getOpenCount(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)I")
  )
  private int getReachAccountingOpenCount(final ContainerOpenersCounter counter, final Level level, final BlockPos pos) {
    final int x = pos.getX();
    final int y = pos.getY();
    final int z = pos.getZ();
    // Maximum reach distance applicable from beacons. Unsure how to generalize this for upstream library
    final double maxRange = 5.0 + (5.0 * 3);
    final AABB aabb = new AABB(x - maxRange, y - maxRange, z - maxRange, x + maxRange, y + maxRange, z + maxRange);
    int count = 0;
    for (final Player player : level.getEntitiesOfClass(Player.class, aabb)) {
      if (this.isOwnContainer(player)) {
        final double reach = ReachEntityAttributes.getReachDistance(player, 5.0);
        final AABB testAabb = new AABB(x - reach, y - reach, z - reach, x + reach, y + reach, z + reach);
        if (player.getBoundingBox().intersects(testAabb)) {
          ++count;
        }
      }
    }
    return count;
  }
}
