package dev.sapphic.beacons.mixin;

import dev.sapphic.beacons.MutableTieredBeacon;
import dev.sapphic.beacons.PotencyTier;
import dev.sapphic.beacons.TieredBeacon;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BeaconBlockEntity.class)
abstract class BeaconBlockEntityMixin extends BlockEntity implements MenuProvider, MutableTieredBeacon {
  @Shadow int levels;
  @Unique private PotencyTier tier = PotencyTier.NONE;

  BeaconBlockEntityMixin(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
    super(type, pos, state);
  }

  @Inject(
      method =
          "tick(" 
              + "Lnet/minecraft/world/level/Level;" 
              + "Lnet/minecraft/core/BlockPos;" 
              + "Lnet/minecraft/world/level/block/state/BlockState;" 
              + "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;" 
              + ")V",
      require = 1,
      allow = 1,
      at = @At(
          shift = At.Shift.BY,
          by = 2,
          value = "INVOKE",
          opcode = Opcodes.INVOKESTATIC,
          target =
              "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;" 
                  + "updateBase(" 
                  + "Lnet/minecraft/world/level/Level;" 
                  + "I" 
                  + "I" 
                  + "I" 
                  + ")I"),
      locals = LocalCapture.CAPTURE_FAILHARD)
  private static void updateTier(
      final Level level, final BlockPos pos, final BlockState state, final BeaconBlockEntity beacon,
      final CallbackInfo ci, final int x, final int y, final int z) {
    var tier = PotencyTier.HIGH;
    var layerOffset = 1;

    layerCheck:
    while (layerOffset <= 4) {
      final var yOffset = y - layerOffset;

      if (yOffset < level.getMinBuildHeight()) {
        tier = PotencyTier.NONE;
        break;
      }

      for (var xOffset = x - layerOffset; xOffset <= (x + layerOffset); ++xOffset) {
        for (var zOffset = z - layerOffset; zOffset <= (z + layerOffset); ++zOffset) {
          final var stateAt = level.getBlockState(new BlockPos(xOffset, yOffset, zOffset));

          if (!stateAt.is(BlockTags.BEACON_BASE_BLOCKS)) {
            if (layerOffset == 1) {
              tier = PotencyTier.NONE;
            }

            break layerCheck;
          }

          final PotencyTier tierAt;

          if (stateAt.is(PotencyTier.HIGH_POTENCY_BLOCKS)) {
            tierAt = PotencyTier.HIGH;
          } else if (stateAt.is(PotencyTier.LOW_POTENCY_BLOCKS)) {
            tierAt = PotencyTier.LOW;
          } else {
            tierAt = PotencyTier.NONE;
          }

          if (tierAt.ordinal() < tier.ordinal()) {
            tier = tierAt;
          }
        }
      }

      ++layerOffset;
    }

    ((MutableTieredBeacon) beacon).setTier(tier);
  }

  @ModifyVariable(
      method =
          "applyEffects(" 
              + "Lnet/minecraft/world/level/Level;" 
              + "Lnet/minecraft/core/BlockPos;" 
              + "I" 
              + "Lnet/minecraft/world/effect/MobEffect;" 
              + "Lnet/minecraft/world/effect/MobEffect;" 
              + ")V",
      require = 1,
      allow = 1,
      at = @At(
          value = "STORE",
          opcode = Opcodes.DSTORE,
          ordinal = 0),
      index = 5)
  private static double modifyEffectRadius(
      final double radius, final Level level, final BlockPos pos) {
    if (level.getBlockEntity(pos) instanceof final TieredBeacon beacon) {
      return radius + (10.0 * beacon.getTier().ordinal());
    }

    return radius; // (levels * 10) + 10
  }

  @ModifyVariable(
      method =
          "applyEffects(" 
              + "Lnet/minecraft/world/level/Level;" 
              + "Lnet/minecraft/core/BlockPos;" 
              + "I" 
              + "Lnet/minecraft/world/effect/MobEffect;" 
              + "Lnet/minecraft/world/effect/MobEffect;" 
              + ")V",
      require = 1,
      allow = 1,
      at = @At(
          value = "STORE",
          opcode = Opcodes.ISTORE,
          ordinal = 0),
      index = 7)
  private static int modifyPrimaryAmplifier(
      final int primaryAmplifier, final Level level, final BlockPos pos, final int levels,
      final @Nullable MobEffect primaryEffect) {
    if (primaryEffect != MobEffects.NIGHT_VISION) {
      if (level.getBlockEntity(pos) instanceof final TieredBeacon beacon) {
        return beacon.getTier().ordinal();
      }
    }

    return primaryAmplifier; // 0
  }

  @ModifyVariable(
      method =
          "applyEffects(" 
              + "Lnet/minecraft/world/level/Level;" 
              + "Lnet/minecraft/core/BlockPos;" 
              + "I" 
              + "Lnet/minecraft/world/effect/MobEffect;" 
              + "Lnet/minecraft/world/effect/MobEffect;" 
              + ")V",
      require = 1,
      allow = 1,
      at = @At(
          value = "STORE",
          opcode = Opcodes.ISTORE,
          ordinal = 1),
      index = 7)
  private static int modifyPotentPrimaryAmplifier(
      final int primaryAmplifier, final Level level, final BlockPos pos, final int levels,
      final @Nullable MobEffect primaryEffect, final @Nullable MobEffect secondaryEffect) {
    if ((primaryEffect != MobEffects.NIGHT_VISION)
        && (secondaryEffect != MobEffects.SLOW_FALLING)
        && (secondaryEffect != MobEffects.FIRE_RESISTANCE)) {
      if (level.getBlockEntity(pos) instanceof final TieredBeacon beacon) {
        return primaryAmplifier + beacon.getTier().ordinal();
      }
    }

    return primaryAmplifier; // 1
  }

  @ModifyVariable(
      method =
          "applyEffects(" 
              + "Lnet/minecraft/world/level/Level;" 
              + "Lnet/minecraft/core/BlockPos;" 
              + "I" 
              + "Lnet/minecraft/world/effect/MobEffect;" 
              + "Lnet/minecraft/world/effect/MobEffect;" 
              + ")V",
      require = 1,
      allow = 1,
      at = @At(
          value = "STORE",
          opcode = Opcodes.ISTORE,
          ordinal = 0),
      index = 8)
  private static int modifyDuration(
      final int duration, final Level level, final BlockPos pos, final int levels) {
    if (level.getBlockEntity(pos) instanceof final TieredBeacon beacon) {
      return ((9 * (beacon.getTier().ordinal() + 1)) + (levels * 2)) * 20;
    }

    return duration; // (9 + levels * 2) * 20
  }

  // Cannot use ModifyArg here as we need to capture the target method parameters
  @ModifyConstant(
      method =
          "applyEffects(" 
              + "Lnet/minecraft/world/level/Level;" 
              + "Lnet/minecraft/core/BlockPos;" 
              + "I" 
              + "Lnet/minecraft/world/effect/MobEffect;" 
              + "Lnet/minecraft/world/effect/MobEffect;" 
              + ")V",
      require = 1,
      allow = 1,
      constant = @Constant(intValue = 0, ordinal = 1))
  private static int modifySecondaryAmplifier(
      final int secondaryAmplifier, final Level level, final BlockPos pos, final int levels,
      final @Nullable MobEffect primaryEffect, final @Nullable MobEffect secondaryEffect) {
    if ((secondaryEffect != MobEffects.SLOW_FALLING)
        && (secondaryEffect != MobEffects.FIRE_RESISTANCE)) {
      if (level.getBlockEntity(pos) instanceof final TieredBeacon beacon) {
        return beacon.getTier().ordinal();
      }
    }

    return secondaryAmplifier; // 0
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

  @Mixin(targets = "net.minecraft.world.level.block.entity.BeaconBlockEntity$1")
  private abstract static class DataAccessMixin implements ContainerData {
    @Final
    @Shadow(aliases = "this$0")
    @MonotonicNonNull
    BeaconBlockEntity this$0;

    @Inject(method = "get(" + "I" + ")I", require = 1, at = @At("HEAD"), cancellable = true)
    private void tryGetTier(final int index, final CallbackInfoReturnable<Integer> cir) {
      if (index == 3) {
        cir.setReturnValue(((TieredBeacon) this.this$0).getTier().ordinal());
      }
    }

    @Inject(method = "set(" + "I" + "I" + ")V", require = 1, at = @At("HEAD"), cancellable = true)
    private void trySetTier(final int index, final int value, final CallbackInfo ci) {
      if (index == 3) {
        ((MutableTieredBeacon) this.this$0).setTier(PotencyTier.values()[value]);
        ci.cancel();
      }
    }

    @ModifyConstant(
        method = "getCount(" + ")I",
        require = 1,
        allow = 1,
        constant = @Constant(intValue = 3))
    private int expandDataCount(final int count) {
      return 3 + 1;
    }
  }
}
