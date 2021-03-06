package dev.sapphic.beacons.mixin;

import dev.sapphic.beacons.PotencyTier;
import dev.sapphic.beacons.MutableTieredBeacon;
import dev.sapphic.beacons.TieredBeacon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(BeaconBlockEntity.class)
abstract class BeaconBlockEntityMixin extends BlockEntity implements MenuProvider, MutableTieredBeacon {
  @Shadow @Final @Mutable private ContainerData dataAccess;

  @Unique
  private PotencyTier tier = PotencyTier.NONE;

  BeaconBlockEntityMixin(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
    super(type, pos, state);
  }

  @Unique
  @Override
  public final PotencyTier getTier() {
    return this.tier;
  }

  @Unique
  @Override
  public final void setTier(final PotencyTier tier) {
    this.tier = Objects.requireNonNull(tier);
  }

  @Redirect(
    method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;)V",
    require = 1, allow = 1,
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKESTATIC,
      target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;updateBase(Lnet/minecraft/world/level/Level;III)I"))
  private static int updateBaseAndTier(
    final Level level, final int x, final int y, final int z,
    // Enclosing method parameters
    final Level level1, final BlockPos pos, final BlockState state, final BeaconBlockEntity beacon
  ) {
    return TieredBeacon.updateBaseAndTier(beacon, level, x, y, z);
  }

  // TODO Rewrite as variable and argument modifications?
  @Redirect(
    method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;)V",
    require = 1, allow = 1,
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKESTATIC,
      target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/world/effect/MobEffect;Lnet/minecraft/world/effect/MobEffect;)V"))
  private static void applyTieredEffects(
    final Level level, final BlockPos pos, final int levels, final @Nullable MobEffect primary,
    final @Nullable MobEffect secondary,
    // Enclosing method parameters
    final Level level1, final BlockPos pos1, final BlockState state, final BeaconBlockEntity beacon
  ) {
    TieredBeacon.applyTieredEffects(beacon, level, pos, levels, primary, secondary);
  }

  @Mixin(targets = "net.minecraft.world.level.block.entity.BeaconBlockEntity$1")
  private abstract static class DataAccessMixin implements ContainerData {
    @Shadow(aliases = "this$0") @Final BeaconBlockEntity this$0;

    @Inject(method = "get(I)I", at = @At("HEAD"), cancellable = true)
    private void tryGetTier(final int index, final CallbackInfoReturnable<Integer> cir) {
      if (index == 3) {
        cir.setReturnValue(PotencyTier.get(this.this$0).ordinal());
      }
    }

    @Inject(method = "set(II)V", at = @At("HEAD"), cancellable = true)
    private void trySetTier(final int index, final int value, final CallbackInfo ci) {
      if (index == 3) {
        PotencyTier.set(this.this$0, PotencyTier.valueOf(value));
        ci.cancel();
      }
    }

    @ModifyConstant(method = "getCount()I", constant = @Constant(intValue = 3))
    private int expandDataCount(final int count) {
      return 4;
    }
  }
}
