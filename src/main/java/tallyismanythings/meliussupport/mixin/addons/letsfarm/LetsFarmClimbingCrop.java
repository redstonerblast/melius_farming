package tallyismanythings.meliussupport.mixin.addons.letsfarm;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tallyismanythings.meliussupport.util.GrowthHandler;
import tallyismanythings.meliussupport.util.ModProperties;

@Mixin(ClimbingCropBlock.class)
public abstract class LetsFarmClimbingCrop extends Block {
    @Shadow
    protected abstract IntProperty getAgeProperty();

    @Shadow
    protected abstract int getMaxAge();

    @Unique
    private static final IntProperty REGROWTH = ModProperties.REGROWTH;

    public LetsFarmClimbingCrop(Settings settings) {
        super(settings);
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void meliusSupport$growthOverride(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "<init>(Lnet/minecraft/block/AbstractBlock$Settings;Lnet/minecraft/util/shape/VoxelShape;)V", at = @At("TAIL"))
    private void supportMelius$RegisterRegrowth(AbstractBlock.Settings properties, VoxelShape shape, CallbackInfo ci) {
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(REGROWTH, 0));
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    private void supportMelius$RegisterRegrowthProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(REGROWTH);
    }

    @ModifyArg(method = "dropFruits(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"), index = 1)
    private BlockState supportMelius$setRegrowth(BlockState state, @Local(argsOnly = true, name = "level") World level) {

        return state.with(REGROWTH, GrowthHandler.getRegrowCount(state.getBlock(), level)).with(getAgeProperty(), getMaxAge() - 1);
    }
}
