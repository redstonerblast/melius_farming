package tallyismanythings.meliussupport.mixin.addons.herbalbrew;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.satisfy.herbalbrews.core.blocks.CoffeeCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tallyismanythings.meliussupport.util.GrowthHandler;
import tallyismanythings.meliussupport.util.ModProperties;

@Mixin(CoffeeCropBlock.class)
public class HerbalBrewCoffeeMixin extends CropBlock {
    @Unique
    private static final IntProperty REGROWTH = ModProperties.REGROWTH;

    public HerbalBrewCoffeeMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    public IntProperty getAgeProperty() {
        return null;
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    private void supportMelius$RegisterRegrowthProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(REGROWTH);
    }

    @Inject(method = "<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V", at = @At("TAIL"))
    private void supportMelius$RegisterRegrowth(AbstractBlock.Settings settings, CallbackInfo ci) {
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(REGROWTH, 0));
    }

    @ModifyConstant(method = "onUseWithItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ItemActionResult;", constant = @Constant(intValue = 1, ordinal = 1))
    public int meliusSupport$properAge(int constant) {
        return 2;
    }

    @Redirect(method = "onUseWithItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ItemActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public boolean meliusSupport$strawberryRegrowthTime(World instance, BlockPos pos, BlockState state, int flags) {
        return instance.setBlockState(pos, state.with(getAgeProperty(), 2).with(REGROWTH, GrowthHandler.getRegrowCount(state.getBlock(), instance)), flags);
    }
}
