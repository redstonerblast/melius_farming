package tallyismanythings.meliussupport.addons.letsdofarm;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.satisfy.farm_and_charm.core.block.RopeBlock;
import net.satisfy.farm_and_charm.core.block.crops.*;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import snownee.jade.api.BlockAccessor;
import tallyismanythings.meliussupport.MeliusSupport;
import tallyismanythings.meliussupport.mixin.accessors.LetsFarmClimbingCropAccessor;
import tallyismanythings.meliussupport.util.GrowthHandler;

public class LetsFarmGrowthHandler {
    public static void handleGrow(BlockState state, ServerWorld world, BlockPos pos) {
        if (state.getBlock() instanceof OnionCropBlock || state.getBlock() instanceof LettuceCropBlock) {
            if (state.getOrEmpty(GrowthHandler.REGROWTH).isPresent() && state.get(GrowthHandler.REGROWTH) > 1) {
                world.setBlockState(pos, state.with(GrowthHandler.REGROWTH, state.get(GrowthHandler.REGROWTH) - 1), Block.NOTIFY_LISTENERS);
            } else {
                if (state.getOrEmpty(GrowthHandler.REGROWTH).isPresent()) {
                    state = state.with(GrowthHandler.REGROWTH, 0);
                }
                if (state.getBlock() instanceof CropBlock crop) {
                    int i = crop.getAge(state);
                    if (i < crop.getMaxAge()) {
                        if (i == crop.getMaxAge() - 1) {
                            GrowthHandler.changeSoil(state, world, pos);
                            growBigCrop(state, world, pos, crop.getMaxAge());
                        } else {
                            world.setBlockState(pos, crop.withAge(i + 1), 3);
                        }
                    }
                }
            }

            return;
        }

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
            if ((state.getOrEmpty(GrowthHandler.REGROWTH).isEmpty() || state.get(GrowthHandler.REGROWTH) == 0) && i + 1 == ((LetsFarmClimbingCropAccessor) climbingCropBlock).meliusSupport$getMaxAge()) {
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
            LetsFarmGrowthHandler.handleGrow(rope, world, up);
        }

        if (i < ((LetsFarmClimbingCropAccessor) climbingCropBlock).meliusSupport$getMaxAge()) {
            boolean ropeOk = rope.getBlock() instanceof RopeBlock && (!rope.contains(RopeBlock.DOWN) || rope.get(RopeBlock.DOWN)) && (!rope.contains(RopeBlock.CENTER_PIECE) || rope.get(RopeBlock.CENTER_PIECE)) && (!rope.contains(RopeBlock.ROPE_KNOT) || !(Boolean)rope.get(RopeBlock.ROPE_KNOT)) && (!rope.contains(RopeBlock.SUPPORTING_ROPE_KNOT) || !(Boolean)rope.get(RopeBlock.SUPPORTING_ROPE_KNOT));
            if (state.get(ClimbingCropBlock.SUPPORTED) && ropeOk && getHeight(pos, world) < getMaxHeight(world, pos)) {
                boolean supported = state.get(ClimbingCropBlock.SUPPORTED);
                BlockState bodyAtHead = (Registries.BLOCK.get(Identifier.of("farm_and_charm","tomato_crop_body"))).getDefaultState().with(ageProp, state.get(ageProp) + 1).with(ClimbingCropBlock.SUPPORTED, supported);
                world.setBlockState(pos, bodyAtHead, 2);
                world.setBlockState(up, (Registries.BLOCK.get(Identifier.of("farm_and_charm","tomato_crop"))).getDefaultState().with(ageProp, 0).with(ClimbingCropBlock.SUPPORTED, supported));
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

    private static void growBigCrop(BlockState state, ServerWorld world, BlockPos pos, int maxAge) {
        IntProperty age = ((BigCropCapable) state.getBlock()).getAgeProperty();
        BooleanProperty big = ((BigCropCapable) state.getBlock()).getBigProperty();
        BooleanProperty giant = ((BigCropCapable) state.getBlock()).getGiantProperty();

        if (state.getOrEmpty(age).isEmpty() || state.getOrEmpty(big).isEmpty() || state.getOrEmpty(giant).isEmpty()) return;

        Random random = world.getRandom();

        if (random.nextFloat() < MeliusSupport.Config.hugeCropChance) {
            world.setBlockState(pos, state.with(giant, true).with(age, maxAge), 3);
        } else if (random.nextFloat() < MeliusSupport.Config.bigCropChance) {
            world.setBlockState(pos, state.with(big, true).with(age, maxAge), 3);
        } else {
            world.setBlockState(pos, state.with(age, maxAge), 3);
        }
    }

    public static int getDays(BlockAccessor accessor) {
        return GrowthHandler.getFullGrowthTime(accessor.getBlockState(), accessor.getLevel(), accessor.getPosition(), ((LetsFarmClimbingCropAccessor) accessor.getBlock()).meliusSupport$getMaxAge(), ((LetsFarmClimbingCropAccessor) accessor.getBlock()).meliusSupport$getAgeProperty());
    }
}
