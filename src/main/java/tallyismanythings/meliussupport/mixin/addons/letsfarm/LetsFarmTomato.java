package tallyismanythings.meliussupport.mixin.addons.letsfarm;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.satisfy.farm_and_charm.core.block.crops.TomatoCropHeadBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TomatoCropHeadBlock.class)
public class LetsFarmTomato {
    @Inject(method = "randomTick", cancellable = true, at = @At("HEAD"))
    public void farmingMelius$cancelGrowth(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        ci.cancel();
    }
}
