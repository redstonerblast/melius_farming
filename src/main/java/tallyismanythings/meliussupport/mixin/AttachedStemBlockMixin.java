package tallyismanythings.meliussupport.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tallyismanythings.meliussupport.util.GrowthHandler;
import tallyismanythings.meliussupport.util.ModProperties;

@Mixin(AttachedStemBlock.class)
public class AttachedStemBlockMixin {

    @Unique
    private static final IntProperty REGROWTH = ModProperties.REGROWTH;

    @ModifyArg(method = "<init>(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/block/AbstractBlock$Settings;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AttachedStemBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"))
    private BlockState supportMelius$RegisterRegrowth(BlockState par1) {
        return par1.with(REGROWTH, 0);
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    private void supportMelius$RegisterRegrowthProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(REGROWTH);
    }

    @ModifyReturnValue(method = "getStateForNeighborUpdate", at = @At(value = "RETURN", ordinal = 0))
    private BlockState supportMelius$CarryRegrowth(BlockState original, @Local(argsOnly = true) WorldAccess worldAccess) {
        return original.with(REGROWTH, GrowthHandler.getRegrowCount(original.getBlock(), worldAccess));
    }
}
