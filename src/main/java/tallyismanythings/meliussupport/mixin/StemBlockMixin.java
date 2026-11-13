package tallyismanythings.meliussupport.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StemBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tallyismanythings.meliussupport.util.ModProperties;

@Mixin(StemBlock.class)
public class StemBlockMixin {

    @Unique
    private static final IntProperty REGROWTH = ModProperties.REGROWTH;

    @Inject(method = "randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)V", at = @At("HEAD"), cancellable = true)
    private void injection(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        ci.cancel();
    }

    @ModifyArg(method = "<init>(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/block/AbstractBlock$Settings;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/StemBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"))
    private BlockState supportMelius$RegisterRegrowth(BlockState par1) {
        return par1.with(REGROWTH, 0);
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    private void supportMelius$RegisterRegrowthProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(REGROWTH);
    }

}
