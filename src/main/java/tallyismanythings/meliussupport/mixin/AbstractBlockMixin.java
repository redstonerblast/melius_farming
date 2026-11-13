package tallyismanythings.meliussupport.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tallyismanythings.meliussupport.blocks.MeliusBlocks;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockMixin {
    @Shadow
    public abstract Block getBlock();

    @Inject(method = "isOf(Lnet/minecraft/block/Block;)Z", at = @At("HEAD"), cancellable = true)
    private void injection(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (block == Blocks.FARMLAND) {
            cir.setReturnValue(getBlock() == block || getBlock() == MeliusBlocks.FARMLAND);
        } else {
            cir.setReturnValue(getBlock() == block);
        }
    }
}
