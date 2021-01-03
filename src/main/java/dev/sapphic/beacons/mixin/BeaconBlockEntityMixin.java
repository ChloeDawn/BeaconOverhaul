package dev.sapphic.beacons.mixin;

import dev.sapphic.beacons.BeaconTier;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(BeaconBlockEntity.class)
abstract class BeaconBlockEntityMixin extends BlockEntity implements MenuProvider, MutableTieredBeacon {
  @Shadow @Final @Mutable private ContainerData dataAccess;

  @Unique
  private BeaconTier tier = BeaconTier.IRON;

  BeaconBlockEntityMixin(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
    super(type, pos, state);
  }

  @Unique
  @Override
  public final BeaconTier getTier() {
    return this.tier;
  }

  @Unique
  @Override
  public final void setTier(final BeaconTier tier) {
    this.tier = Objects.requireNonNull(tier);
  }

  @Redirect(method = "tick", at = @At(value = "INVOKE",
    target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;updateBase(Lnet/minecraft/world/level/Level;III)I"),
    require = 1, allow = 1)
  private static int updateBaseAndTier(
    final Level level, final int x, final int y, final int z,
    // Enclosing method parameters
    final Level level1, final BlockPos pos, final BlockState state, final BeaconBlockEntity beacon
  ) {
    return TieredBeacon.updateBaseAndTier(beacon, level, x, y, z);
  }

  // TODO Rewrite as variable and argument modifications?
  @Redirect(method = "tick", at = @At(value = "INVOKE",
    target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/world/effect/MobEffect;Lnet/minecraft/world/effect/MobEffect;)V"),
    require = 1, allow = 1)
  private static void applyTieredEffects(
    final Level level, final BlockPos pos, final int levels, final @Nullable MobEffect primary,
    final @Nullable MobEffect secondary,
    // Enclosing method parameters
    final Level level1, final BlockPos pos1, final BlockState state, final BeaconBlockEntity beacon
  ) {
    TieredBeacon.applyTieredEffects(beacon, level, pos, levels, primary, secondary);
  }

  @Inject(method = "<init>", at = @At("RETURN"), require = 1, allow = 1)
  private void injectTierData(final CallbackInfo ci) {
    final ContainerData delegate = this.dataAccess;

    if (delegate.getCount() != 3) {
      throw new IllegalStateException("Unsupported container data " + delegate);
    }

    this.dataAccess = new ContainerData() {
      @Override
      public int get(final int index) {
        if (index == 3) {
          return BeaconBlockEntityMixin.this.tier.ordinal();
        }

        return delegate.get(index);
      }

      @Override
      public void set(final int index, final int value) {
        if (index == 3) {
          BeaconBlockEntityMixin.this.tier = BeaconTier.valueOf(value);
        } else {
          delegate.set(index, value);
        }
      }

      @Override
      public int getCount() {
        return 4;
      }
    };
  }
}
