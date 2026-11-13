package tallyismanythings.meliussupport.mixin.addons.icyhorse;

import com.llamalad7.mixinextras.sugar.Local;
import icy.betterhorses.net.entity.HorseCartEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tallyismanythings.meliussupport.blocks.MeliusBlocks;

@Mixin(HorseCartEntity.class)
public class IcyHorsePloughMixin {
    @Redirect(method = "turnOver(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDefaultState()Lnet/minecraft/block/BlockState;"))
    public BlockState meliusFarming$replaceFarmland(Block instance, @Local(argsOnly = true) ServerWorld level, @Local(argsOnly = true, name = "pos") BlockPos pos) {
        level.scheduleBlockTick(pos, MeliusBlocks.FARMLAND, 10);
        return MeliusBlocks.FARMLAND.getDefaultState();
    }
}
