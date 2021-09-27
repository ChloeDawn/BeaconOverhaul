package dev.sapphic.beacons.mixin;

import dev.sapphic.beacons.MutableTieredBeacon;
import dev.sapphic.beacons.PotencyTier;
import dev.sapphic.beacons.TieredBeacon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(BeaconBlockEntity.class)
abstract class BeaconBlockEntityMixin extends BlockEntity implements MenuProvider, MutableTieredBeacon {
  @Unique private PotencyTier tier = PotencyTier.NONE;

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

  @ModifyVariable(
          method = "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/world/effect/MobEffect;Lnet/minecraft/world/effect/MobEffect;)V",
          at=@At(value = "STORE"),
          ordinal = 0
  )
  private static double updateDistance(double distance, Level level, BlockPos blockPos, int i, @Nullable MobEffect mobEffect, @Nullable MobEffect mobEffect2){
    @Nullable final BlockEntity blockEntity = level.getBlockEntity(blockPos);
    if (!(blockEntity instanceof BeaconBlockEntity)) return distance;
    return 10.0 * PotencyTier.get(blockEntity).ordinal() + distance;
  }
  @ModifyVariable(
          method = "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/world/effect/MobEffect;Lnet/minecraft/world/effect/MobEffect;)V",
          at=@At(value = "STORE", ordinal = 0),
          ordinal = 1
  )
  private static int updatePrimaryAmplifier(int amplifier, Level level, BlockPos blockPos, int i, @Nullable MobEffect mobEffect, @Nullable MobEffect mobEffect2){
    @Nullable final BlockEntity blockEntity = level.getBlockEntity(blockPos);
    if ((!(blockEntity instanceof BeaconBlockEntity)) || mobEffect == MobEffects.NIGHT_VISION) {
      return amplifier;
    }
    return PotencyTier.get(blockEntity).ordinal() + amplifier;
  }
  @ModifyVariable(
          method = "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/world/effect/MobEffect;Lnet/minecraft/world/effect/MobEffect;)V",
          at = @At(value = "STORE", ordinal = 1),
          ordinal = 1
  )
  private static int updatePrimaryAmplifierAgain(int amplifier, Level level, BlockPos blockPos, int i, @Nullable MobEffect primary, @Nullable MobEffect secondary){
    @Nullable final BlockEntity blockEntity = level.getBlockEntity(blockPos);
    if ((!(blockEntity instanceof BeaconBlockEntity)) || secondary == MobEffects.SLOW_FALLING || secondary == MobEffects.FIRE_RESISTANCE || primary == MobEffects.NIGHT_VISION) {
      return amplifier;
    }
    return PotencyTier.get(blockEntity).ordinal() + amplifier;
  }
  @ModifyVariable(
          method = "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/world/effect/MobEffect;Lnet/minecraft/world/effect/MobEffect;)V",
          at=@At("STORE"),
          ordinal = 2
  )
  private static int updateDuration(int duration, Level level, BlockPos blockPos, int i, @Nullable MobEffect mobEffect, @Nullable MobEffect mobEffect2){
    @Nullable final BlockEntity blockEntity = level.getBlockEntity(blockPos);
    if (!(blockEntity instanceof BeaconBlockEntity)) return duration;
    return 9 * PotencyTier.get(blockEntity).ordinal() + duration;
  }

  @ModifyConstant(
          method = "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/world/effect/MobEffect;Lnet/minecraft/world/effect/MobEffect;)V",
          constant = @Constant(intValue = 0, ordinal = 1)
  )
  private static int updateSecondaryAmplifier(int amplifier, Level level, BlockPos blockPos, int i, @Nullable MobEffect primary, @Nullable MobEffect secondary){
    @Nullable final BlockEntity blockEntity = level.getBlockEntity(blockPos);
    if ((!(blockEntity instanceof BeaconBlockEntity)) || secondary == MobEffects.SLOW_FALLING || secondary == MobEffects.FIRE_RESISTANCE) {
      return 0;
    }
    return PotencyTier.get(blockEntity).ordinal();
  }

  @Inject(
          method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;)V",
          at = @At(value = "INVOKE", opcode = Opcodes.INVOKESTATIC,
                  target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;updateBase(Lnet/minecraft/world/level/Level;III)I"))
  private static void updateBaseAndTier(Level level, BlockPos blockPos, BlockState blockState, BeaconBlockEntity beacon, CallbackInfo ci) {
    TieredBeacon.updateBaseAndTier(beacon, level, blockPos.getX(), blockPos.getY(), blockPos.getZ());
  }

  @Mixin(targets = "net.minecraft.world.level.block.entity.BeaconBlockEntity$1")
  private abstract static class DataAccessMixin implements ContainerData {
    @Shadow(aliases = "this$0") @Final BeaconBlockEntity this$0;

    @Inject(method = "get(I)I", require = 1, at = @At("HEAD"), cancellable = true)
    private void tryGetTier(final int index, final CallbackInfoReturnable<Integer> cir) {
      if (index == 3) {
        cir.setReturnValue(PotencyTier.get(this.this$0).ordinal());
      }
    }

    @Inject(method = "set(II)V", require = 1, at = @At("HEAD"), cancellable = true)
    private void trySetTier(final int index, final int value, final CallbackInfo ci) {
      if (index == 3) {
        PotencyTier.set(this.this$0, PotencyTier.valueOf(value));
        ci.cancel();
      }
    }

    @ModifyConstant(method = "getCount()I",
      require = 1, allow = 1,
      constant = @Constant(intValue = 3))
    private int expandDataCount(final int count) {
      return 3 + 1;
    }
  }
}
