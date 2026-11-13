package tallyismanythings.meliussupport.addons.letsdobrewery;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.satisfy.farm_and_charm.core.block.RopeBlock;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import tallyismanythings.meliussupport.mixin.accessors.LetsFarmClimbingCropAccessor;
import tallyismanythings.meliussupport.util.GrowthHandler;

public class LetsBrewGrowthHandler {
    public static void handleGrow(BlockState state, ServerWorld world, BlockPos pos) {
        if (state.getBlock() instanceof ClimbingCropBlock climbingCropBlock) {
            growClimbing(state, world, pos, climbingCropBlock);
            return;
        }

        GrowthHandler.growOrRegrowth(state, world, pos);
    }

    public static void growClimbing(BlockState state, ServerWorld world, BlockPos pos, ClimbingCropBlock climbingCropBlock) {
        IntProperty ageProp = ((LetsFarmClimbingCropAccessor) climbingCropBlock).meliusSupport$getAgeProperty();
        int i = state.get(ageProp);

        if (state.getOrEmpty(GrowthHandler.REGROWTH).isPresent() && state.get(GrowthHandler.REGROWTH) > 1) {
            world.setBlockState(pos, state.with(GrowthHandler.REGROWTH, state.get(GrowthHandler.REGROWTH) - 1), Block.NOTIFY_LISTENERS);
        } else {
            if (state.getOrEmpty(GrowthHandler.REGROWTH).isEmpty() || state.get(GrowthHandler.REGROWTH) == 0 && i + 1 == ((LetsFarmClimbingCropAccessor) climbingCropBlock).meliusSupport$getMaxAge()) {
                GrowthHandler.changeSoil(state, world, pos);
            }
            if (state.getOrEmpty(GrowthHandler.REGROWTH).isPresent()) {
                state = state.with(GrowthHandler.REGROWTH, 0);
            }
            if (i < ((LetsFarmClimbingCropAccessor) climbingCropBlock).meliusSupport$getMaxAge()) {
                world.setBlockState(pos, state.with(ageProp, i + 1), 3);
            }
        }

        BlockPos up = pos.up();
        BlockState rope = world.getBlockState(up);

        if (rope.getBlock() instanceof ClimbingCropBlock) {
            LetsBrewGrowthHandler.handleGrow(rope, world, up);
        }

        if (i < ((LetsFarmClimbingCropAccessor) climbingCropBlock).meliusSupport$getMaxAge()) {
            boolean ropeOk = rope.getBlock() instanceof RopeBlock && (!rope.contains(RopeBlock.DOWN) || rope.get(RopeBlock.DOWN)) && (!rope.contains(RopeBlock.CENTER_PIECE) || rope.get(RopeBlock.CENTER_PIECE)) && (!rope.contains(RopeBlock.ROPE_KNOT) || !(Boolean)rope.get(RopeBlock.ROPE_KNOT)) && (!rope.contains(RopeBlock.SUPPORTING_ROPE_KNOT) || !(Boolean)rope.get(RopeBlock.SUPPORTING_ROPE_KNOT));
            if (state.get(ClimbingCropBlock.SUPPORTED) && ropeOk && getHeight(pos, world) < getMaxHeight(world, pos)) {
                boolean supported = state.get(ClimbingCropBlock.SUPPORTED);
                BlockState bodyAtHead = (Registries.BLOCK.get(Identifier.of("brewery","hops_crop_body"))).getDefaultState().with(ageProp, state.get(ageProp) + 1).with(ClimbingCropBlock.SUPPORTED, supported);
                world.setBlockState(pos, bodyAtHead, 2);
                world.setBlockState(up, (Registries.BLOCK.get(Identifier.of("brewery","hops_crop"))).getDefaultState().with(ageProp, 0).with(ClimbingCropBlock.SUPPORTED, supported));
            }
        }
    }

    protected static int getHeight(BlockPos pos, WorldAccess level) {
        int h;
        for(h = 0; level.getBlockState(pos.down(h)).getBlock() instanceof ClimbingCropBlock; ++h) {
        }

        return h;
    }

    public static int getMaxHeight(WorldAccess level, BlockPos pos) {
        BlockState self = level.getBlockState(pos);
        boolean supportedFlag = self.contains(ClimbingCropBlock.SUPPORTED) && (Boolean)self.get(ClimbingCropBlock.SUPPORTED);
        boolean ropeAbove = level.getBlockState(pos.up()).getBlock() instanceof RopeBlock || level.getBlockState(pos.up(2)).getBlock() instanceof RopeBlock;
        return !supportedFlag && !ropeAbove ? 2 : 4;
    }
}
