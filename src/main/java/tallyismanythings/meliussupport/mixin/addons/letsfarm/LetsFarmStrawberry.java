package tallyismanythings.meliussupport.mixin.addons.letsfarm;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.satisfy.farm_and_charm.core.block.crops.StrawberryCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tallyismanythings.meliussupport.util.GrowthHandler;
import tallyismanythings.meliussupport.util.ModProperties;

@Mixin(StrawberryCropBlock.class)
public abstract class LetsFarmStrawberry extends CropBlock {
    @Unique
    private static final IntProperty REGROWTH = ModProperties.REGROWTH;

    public LetsFarmStrawberry(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V", at = @At("TAIL"))
    private void supportMelius$RegisterRegrowth(AbstractBlock.Settings settings, CallbackInfo ci) {
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(REGROWTH, 0));
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    private void supportMelius$RegisterRegrowthProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(REGROWTH);
    }

    @Shadow
    public abstract IntProperty getAgeProperty();

    @Redirect(method = "onUse(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public boolean meliusSupport$strawberryRegrowthTime(World instance, BlockPos pos, BlockState state, int flags) {
        return instance.setBlockState(pos, state.with(getAgeProperty(), 4).with(REGROWTH, GrowthHandler.getRegrowCount(state.getBlock(), instance)), flags);
    }
}
