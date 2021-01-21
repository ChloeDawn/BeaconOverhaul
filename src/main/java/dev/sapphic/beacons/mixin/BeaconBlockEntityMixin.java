package dev.sapphic.beacons.mixin;

import dev.sapphic.beacons.BeaconTier;
import dev.sapphic.beacons.MutableTieredBeacon;
import dev.sapphic.beacons.TieredBeacon;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.checkerframework.checker.nullness.qual.Nullable;
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

  @Shadow private @Nullable MobEffect primaryPower;
  @Shadow private @Nullable MobEffect secondaryPower;
  @Shadow private int levels;

  @Unique
  private BeaconTier tier = BeaconTier.IRON;

  BeaconBlockEntityMixin(final BlockEntityType<?> type) {
    super(type);
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

  // TODO Rewrite as variable and argument modifications? (1.16)
  @Redirect(method = "tick()V",
    require = 1, allow = 1,
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;updateBase(III)V"))
  private void updateBaseAndTier(final BeaconBlockEntity beacon, final int x, final int y, final int z) {
    assert this.level != null;

    this.levels = TieredBeacon.updateBaseAndTier(beacon, this.level, x, y, z);
  }

  // TODO Rewrite as variable and argument modifications?
  @Redirect(method = "tick()V",
    require = 1, allow = 1,
    at = @At(value = "INVOKE", opcode = Opcodes.INVOKEVIRTUAL,
      target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;applyEffects()V"))
  private void applyTieredEffects(final BeaconBlockEntity beacon) {
    assert this.level != null;

    TieredBeacon.applyTieredEffects(beacon, this.level, this.worldPosition, this.levels, this.primaryPower, this.secondaryPower);
  }

  @Mixin(targets = "net.minecraft.world.level.block.entity.BeaconBlockEntity$1")
  private abstract static class DataAccessMixin implements ContainerData {
    @Shadow(aliases = "this$0") @Final BeaconBlockEntity this$0;

    @Inject(method = "get(I)I", at = @At("HEAD"), cancellable = true)
    private void tryGetTier(final int index, final CallbackInfoReturnable<Integer> cir) {
      if (index == 3) {
        cir.setReturnValue(BeaconTier.get(this.this$0).ordinal());
      }
    }

    @Inject(method = "set(II)V", at = @At("HEAD"), cancellable = true)
    private void trySetTier(final int index, final int value, final CallbackInfo ci) {
      if (index == 3) {
        BeaconTier.set(this.this$0, BeaconTier.valueOf(value));
        ci.cancel();
      }
    }

    @ModifyConstant(method = "getCount()I", constant = @Constant(intValue = 3))
    private int expandDataCount(final int count) {
      return 4;
    }
  }
}
