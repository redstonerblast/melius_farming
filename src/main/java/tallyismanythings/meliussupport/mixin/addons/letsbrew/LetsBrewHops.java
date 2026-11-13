package tallyismanythings.meliussupport.mixin.addons.letsbrew;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.satisfy.brewery.core.block.HopsCropHeadBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HopsCropHeadBlock.class)
public class LetsBrewHops {
    @Inject(method = "randomTick", cancellable = true, at = @At("HEAD"))
    public void farmingMelius$cancelGrowth(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        ci.cancel();
    }
}
