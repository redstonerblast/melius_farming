package tallyismanythings.meliussupport.blocks.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import tallyismanythings.meliussupport.MeliusSupport;
import tallyismanythings.meliussupport.events.ServerEvents;
import tallyismanythings.meliussupport.util.GrowthHandler;
import tallyismanythings.meliussupport.util.ModProperties;
import tallyismanythings.meliussupport.util.Tags;

public class MeliusFarmland extends FarmlandBlock {
    public static final IntProperty ANIMA = ModProperties.ANIMA;
    public static final IntProperty VIGOR = ModProperties.VIGOR;
    public static final IntProperty CORPUS = ModProperties.CORPUS;

    public MeliusFarmland(Settings settings) {
        super(settings);
        this.setDefaultState(getDefaultState().with(ANIMA, 2).with(VIGOR, 2).with(CORPUS, 2));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ANIMA, VIGOR, CORPUS);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.scheduleBlockTick(pos, this, MeliusSupport.Config.farmRefreshRate);

        if (world.hasRain(pos.up())) {
            world.setBlockState(pos, state.with(FarmlandBlock.MOISTURE, 7), 2);
        }

        int timeElipsed = Math.abs(ServerEvents.DAYS - world.getChunk(pos).getAttachedOrElse(MeliusSupport.PERSISTENT_DAY_TRACKER, 0));
        if (timeElipsed != 0) {
            farmUpdate(state, pos, world);
            timeElipsed--;
            boolean water = isKeptWatered(pos, world);
            if (!water & timeElipsed > 0) {
                world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
                return;
            }
            for (;timeElipsed > 0;timeElipsed--) {
                GrowthHandler.farmlandUpdate(state, world, pos.up());
            }
        }

        long dayTime = world.getTimeOfDay() % 24000;

        // check before rain
        if (dayTime >= MeliusSupport.Config.dailyTimeMin && dayTime <= MeliusSupport.Config.dailyTimeMin + MeliusSupport.Config.farmRefreshRate) {
            farmUpdate(state, pos, world);
        }
    }

    public void farmUpdate(BlockState state, BlockPos pos, ServerWorld world) {
        if (state.get(FarmlandBlock.MOISTURE) == 0) {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
        } else {
            world.setBlockState(pos, state.with(FarmlandBlock.MOISTURE, 0), 2);
            GrowthHandler.farmlandUpdate(state, world, pos.up());
        }
    }

    public boolean isKeptWatered(BlockPos pos, ServerWorld world) {
        for(BlockPos blockPos : BlockPos.iterate(pos.add(-5, 1, -5), pos.add(5, 1, 5))) {
            if (world.getBlockState(blockPos).isIn(Tags.WATER_PROVIDER)) {
                return true;
            }
        }

        return false;
    }
}
