package tallyismanythings.meliussupport.mixin;

import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BonemealItemMixin {
    @Inject(method = "useOnFertilizable", at = @At("HEAD"), cancellable = true)
    private static void meliusFarming$noBonemeal(ItemStack stack, World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (world.getBlockState(pos).isIn(BlockTags.CROPS)) {
            cir.setReturnValue(false);
        }
    }
}
