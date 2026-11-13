package tallyismanythings.meliussupport.addons.supplementaries;

import net.mehvahdjukaar.supplementaries.common.block.blocks.FlaxBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import tallyismanythings.meliussupport.util.GrowthHandler;

public class SuppGrowthHandler {
    public static void handleGrow(BlockState state, ServerWorld world, BlockPos pos) {
        if (state.getBlock() instanceof FlaxBlock) {
            growFlax(state, world, pos);
            return;
        }

        GrowthHandler.growOrRegrowth(state, world, pos);
    }

    public static void growFlax(BlockState state, ServerWorld world, BlockPos pos) {
        int age = state.get(CropBlock.AGE);
        if (age >= 3 && age < ((CropBlock) state.getBlock()).getMaxAge()) {
            world.setBlockState(pos.up(), state.with(CropBlock.AGE, age + 1).with(FlaxBlock.HALF, DoubleBlockHalf.UPPER), 2);
        }
        GrowthHandler.onGrow(state, world, pos, true);
    }
}
