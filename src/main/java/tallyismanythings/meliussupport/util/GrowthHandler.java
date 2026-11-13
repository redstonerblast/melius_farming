package tallyismanythings.meliussupport.util;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.resources.CropConfigs;
import io.github.lucaargolo.seasons.utils.FertilizableUtil;
import io.github.lucaargolo.seasons.utils.GreenhouseCache;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import tallyismanythings.meliussupport.MeliusSupport;
import tallyismanythings.meliussupport.addons.letsdobrewery.LetsBrewGrowthHandler;
import tallyismanythings.meliussupport.addons.letsdofarm.LetsFarmGrowthHandler;
import tallyismanythings.meliussupport.addons.supplementaries.SuppGrowthHandler;
import tallyismanythings.meliussupport.blocks.MeliusBlocks;
import tallyismanythings.meliussupport.blocks.custom.MeliusFarmland;
import tallyismanythings.meliussupport.data.CropSoilHandler;
import tallyismanythings.meliussupport.data.MeliusDataResources;
import tallyismanythings.meliussupport.events.ServerEvents;
import tallyismanythings.meliussupport.mixin.accessors.StemBlockAccessor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GrowthHandler {
    public static final IntProperty REGROWTH = ModProperties.REGROWTH;

    public static void farmlandUpdate(BlockState state, ServerWorld world, BlockPos pos) {
        cropUpdate(world.getBlockState(pos), world, pos);
    }

    public static void cropUpdate(BlockState state, ServerWorld world, BlockPos pos) {
        if(state.getBlock() instanceof CropBlock || state.getBlock() instanceof StemBlock || state.isIn(Tags.CROPS)) {
            if (world.getBaseLightLevel(pos, 0) >= 9) {
                float mult = FertilizableUtil.getMultiplier(world, pos, state);
                mult = mult * getSoilMult(world, state, pos);
                if (!world.isClient) {
                    if (mult == 0.0) {
                        world.breakBlock(pos, true);
                    }
                    else if (Math.floor((ServerEvents.DAYS - 1) * mult) != Math.floor(ServerEvents.DAYS * mult)) {
                        int c = (int) (Math.floor(ServerEvents.DAYS * mult) - Math.floor((ServerEvents.DAYS - 1) * mult));
                        for (int i = 0; i < c; i++) {
                            state = world.getBlockState(pos);
                            modSwitchHandle(state, world, pos);
                        }
                    }
                }
            }
        }
    }

    public static float getSoilMult(WorldAccess worldAccess, BlockState state, BlockPos pos) {
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        DynamicRegistryManager dynamicRegistryManager = worldAccess.getRegistryManager();
        Optional<CropSoilHandler> soilHandle = dynamicRegistryManager.get(MeliusDataResources.CROP_HANDLER_KEY)
                .stream().filter((a) -> Identifier.of(a.identifier()).equals(id)).findFirst();
        if (soilHandle.isEmpty()) {
            return 1;
        }
        CropSoilHandler soilHandler = soilHandle.get();
        BlockPos soilPos = pos;
        for (; worldAccess.getBlockState(soilPos).isIn(BlockTags.CROPS); soilPos = soilPos.down()) {}
        BlockState soil = worldAccess.getBlockState(soilPos);
        IntProvider vigor = soilHandler.vigorSuitable();
        IntProvider anima = soilHandler.animaSuitable();
        IntProvider corpus = soilHandler.corpusSuitable();
        int aVigor = soil.get(MeliusFarmland.VIGOR);
        int aAnima = soil.get(MeliusFarmland.ANIMA);
        int aCorpus = soil.get(MeliusFarmland.CORPUS);
        boolean suitable = vigor.getMin() <= aVigor && aVigor <= vigor.getMax() &&
                anima.getMin() <= aAnima && aAnima <= anima.getMax() &&
                corpus.getMin() <= aCorpus && aCorpus <= corpus.getMax();
        boolean ideal = new Vec3i(aVigor, aAnima, aCorpus).equals(soilHandler.idealSoil());
        return ideal ? 1.5f : suitable ? 1.0f : 0.33f;
    }

    public static void modSwitchHandle(BlockState state, ServerWorld world, BlockPos pos) {
        String mod = Registries.BLOCK.getId(state.getBlock()).getNamespace();

        if (Objects.equals(mod, "supplementaries")) {
            SuppGrowthHandler.handleGrow(state, world, pos);
            return;
        }

        if (Objects.equals(mod, "farm_and_charm")) {
            LetsFarmGrowthHandler.handleGrow(state, world, pos);
            return;
        }

        if (Objects.equals(mod, "brewery")) {
            LetsBrewGrowthHandler.handleGrow(state, world, pos);
            return;
        }

        growOrRegrowth(state, world, pos);
    }

    public static void growOrRegrowth(BlockState state, ServerWorld world, BlockPos pos) {
        if (state.getOrEmpty(REGROWTH).isPresent() && state.get(REGROWTH) > 1) {
            world.setBlockState(pos, state.with(REGROWTH, state.get(REGROWTH) - 1), Block.NOTIFY_LISTENERS);
        } else {
            boolean canDeplete = state.getOrEmpty(REGROWTH).isEmpty() || state.get(REGROWTH) == 0;
            if (state.getOrEmpty(REGROWTH).isPresent()) {
                state = state.with(REGROWTH, 0);
            }
            onGrow(state, world, pos, canDeplete);
        }
    }

    public static void onGrow(BlockState state, ServerWorld world, BlockPos pos, boolean canDeplete) {
        if (state.getBlock() instanceof CropBlock crop) {
            int i = crop.getAge(state);
            if (i < crop.getMaxAge()) {
                if (i + 1 == crop.getMaxAge() && canDeplete) {
                    changeSoil(state, world, pos);
                }
                world.setBlockState(pos, crop.withAge(i + 1), 3);
            }
        }
        if (state.getBlock() instanceof PitcherCropBlock pitcherCrop) {
            int i = state.get(PitcherCropBlock.AGE);
            if (i < 4) {
                int j = i + 1;
                BlockState blockState = world.getBlockState(pos.up());
                boolean air = blockState.isAir() || blockState.isOf(Blocks.PITCHER_CROP);
                if (i != 3 || air) {
                    BlockState blockStateb = state.with(PitcherCropBlock.AGE, j);
                    world.setBlockState(pos, blockStateb, 2);
                    if (i == 3) {
                        changeSoil(state, world, pos);
                        world.setBlockState(pos.up(), (BlockState)blockStateb.with(PitcherCropBlock.HALF, DoubleBlockHalf.UPPER), 3);
                    }
                }
            }
        }
        if (state.getBlock() instanceof StemBlock stem) {
            int i = state.get(StemBlock.AGE);
            if (i < 7) {
                world.setBlockState(pos, state.with(StemBlock.AGE, i + 1), Block.NOTIFY_LISTENERS);
            }
            if (i >= 6) {
                if (canDeplete) {
                    changeSoil(state, world, pos);
                }
                List<Direction> directions = Direction.Type.HORIZONTAL.getShuffled(world.random);
                for (Direction direction : directions) {
                    BlockPos blockPos = pos.offset(direction);
                    BlockState blockState = world.getBlockState(blockPos.down());
                    if (world.getBlockState(blockPos).isAir() && (blockState.isOf(Blocks.FARMLAND) || blockState.isIn(BlockTags.DIRT))) {
                        Registry<Block> registry = world.getRegistryManager().get(RegistryKeys.BLOCK);
                        Optional<Block> optional = registry.getOrEmpty(((StemBlockAccessor) stem).meliusSupport$gourdBlock());
                        Optional<Block> optional2 = registry.getOrEmpty(((StemBlockAccessor) stem).meliusSupport$attachedStemBlock());
                        if (optional.isPresent() && optional2.isPresent()) {
                            world.setBlockState(blockPos, optional.get().getDefaultState());
                            world.setBlockState(pos, optional2.get().getDefaultState().with(HorizontalFacingBlock.FACING, direction));
                        }
                        break;
                    }
                }
            }
        }
    }

    public static void changeSoil(BlockState state, ServerWorld world, BlockPos pos) {
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        DynamicRegistryManager dynamicRegistryManager = world.getRegistryManager();
        Optional<CropSoilHandler> soilHandle = dynamicRegistryManager.get(MeliusDataResources.CROP_HANDLER_KEY).stream().filter((a) -> Identifier.of(a.identifier()).equals(id)).findFirst();
        if (soilHandle.isEmpty()) {
            return;
        }
        CropSoilHandler soilHandler = soilHandle.get();
        BlockPos farmPos = pos.down();
        if (world.getBlockState(farmPos).getBlock() != MeliusBlocks.FARMLAND) {
            return;
        }
        BlockState farmland = world.getBlockState(farmPos);
        int vC = farmland.get(MeliusFarmland.VIGOR);
        int aC = farmland.get(MeliusFarmland.ANIMA);
        int cC = farmland.get(MeliusFarmland.CORPUS);
        world.setBlockState(farmPos, farmland
                .with(MeliusFarmland.VIGOR, Math.clamp(vC + soilHandler.soilChange().getX(), 0, 4))
                .with(MeliusFarmland.ANIMA, Math.clamp(aC + soilHandler.soilChange().getY(), 0, 4))
                .with(MeliusFarmland.CORPUS, Math.clamp(cC + soilHandler.soilChange().getZ(), 0, 4))
        );
    }

    public static int getRegrowCount(Block block, WorldAccess worldAccess) {
        Identifier id = Registries.BLOCK.getId(block);
        DynamicRegistryManager dynamicRegistryManager = worldAccess.getRegistryManager();
        Optional<CropSoilHandler> soil = dynamicRegistryManager.get(MeliusDataResources.CROP_HANDLER_KEY).stream().filter((a) -> Identifier.of(a.identifier()).equals(id)).findFirst();
        return soil.map(CropSoilHandler::regrowth).orElse(5);
    }

    public static int getFullRegrowTime(BlockState state, ServerWorld world, BlockPos pos) {
        float mult = FertilizableUtil.getMultiplier(world, pos, state);
        int regrowTime = state.get(REGROWTH);

        return (int) Math.ceil(((Math.floor(mult * ServerEvents.DAYS)) + regrowTime) / mult) - ServerEvents.DAYS;
    }

    public static int getFullRegrowTime(BlockState state, World world, BlockPos pos) {
        return getTimeForStateGrowth(state, world, pos, state.get(REGROWTH));
    }

    public static int getFullGrowthTime(BlockState state, World world, BlockPos pos, int max, IntProperty property) {
        return getTimeForStateGrowth(state, world, pos, max - state.get(property));
    }

    public static int getFullGrowthTimeCrop(BlockState state, World world, BlockPos pos) {
        CropBlock crop = (CropBlock) state.getBlock();
        return getTimeForStateGrowth(state, world, pos, crop.getMaxAge() - crop.getAge(state));
    }

    public static int getTimeForStateGrowth(BlockState state, World world, BlockPos pos, Integer goalTime) {
        float mult = getMultiplierClientWorld(world, pos, state);
        if (mult == 0.0) {
            return -99;
        }
        return (int) Math.ceil(((Math.floor(mult * (int) (world.getTimeOfDay() / 24000L))) + goalTime) / mult) - (int) (world.getTimeOfDay() / 24000L);
    }

    public static float getMultiplierClientWorld(World world, BlockPos pos, BlockState state) {
        float multiplier;
        if (FabricSeasons.CONFIG.doCropsGrowsNormallyUnderground() && world.getLightLevel(LightType.SKY, pos) == 0) {
            multiplier = 1.0F;
        } else {
            Identifier cropIdentifier = Registries.BLOCK.getId(state.getBlock());
            Season warmestSeason = GreenhouseCache.test(world, pos);
            multiplier = CropConfigs.getSeasonCropMultiplier(cropIdentifier, warmestSeason);
        }

        multiplier = multiplier * getSoilMult(world, state, pos);

        return multiplier;
    }
}
