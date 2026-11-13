package tallyismanythings.meliussupport.items.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import tallyismanythings.meliussupport.blocks.MeliusBlocks;
import tallyismanythings.meliussupport.util.LegacyUtil;

import java.util.List;

public class WateringCan extends Item {

    public static final int MAX_WATER_LEVEL = 32;
    public static final String WATER_LEVEL_KEY = "item.melius_farming.watering_can.water_level";

    public WateringCan(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        BlockHitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.WATER);
        ItemStack stack = user.getStackInHand(hand);
        var stackCopy = stack.copy();
        if (hitResult.getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(stack);
        }
        TypedActionResult<ItemStack> result = this.tryUseOnBlock(world, user, stack, hitResult);
        if (result != null) {
            return result;
        }
        return TypedActionResult.pass(stackCopy);
    }

    protected TypedActionResult<ItemStack> tryUseOnBlock(World world, PlayerEntity user, ItemStack stack, BlockHitResult hitResult) {
        Vec3d pos = hitResult.getPos();
        BlockState state = world.getBlockState(hitResult.getBlockPos());
        if (state.getFluidState().isIn(FluidTags.WATER)) {
            if (getWaterLevel(stack) < MAX_WATER_LEVEL) {
                WateringCan.setWaterLevel(world, stack, MAX_WATER_LEVEL);
                world.playSound(pos.x, pos.y, pos.z, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F, true);
                return TypedActionResult.success(stack);
            }
            return TypedActionResult.fail(stack);
        }
        if (!canWater(stack)) {
            return null;
        }
        if (state.isOf(MeliusBlocks.FARMLAND) && state.get(FarmlandBlock.MOISTURE) < 7) {
            if (user.isSneaking()) {
                return null;
            }
            if (!user.isCreative()) {
                WateringCan.setWaterLevel(world, stack, WateringCan.getWaterLevel(stack) - 1);
            }
            world.setBlockState(
                    hitResult.getBlockPos(),
                    world.getBlockState(hitResult.getBlockPos())
                            .with(FarmlandBlock.MOISTURE, 7)
            );
            world.playSound(pos.x, pos.y, pos.z, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.0F, true);
            return TypedActionResult.success(stack);
        }
        if (state.isIn(BlockTags.CROPS)) {
            if (user.isSneaking()) {
                return null;
            }
            BlockPos newBlock = hitResult.getBlockPos();

            for (; world.getBlockState(newBlock).isIn(BlockTags.CROPS); newBlock = newBlock.down()) {}

            if (world.getBlockState(newBlock).isOf(MeliusBlocks.FARMLAND) && world.getBlockState(newBlock).get(FarmlandBlock.MOISTURE) < 7) {
                if (!user.isCreative()) {
                    WateringCan.setWaterLevel(world, stack, WateringCan.getWaterLevel(stack) - 1);
                }
                world.setBlockState(
                        newBlock,
                        world.getBlockState(newBlock)
                                .with(FarmlandBlock.MOISTURE, 7)
                );
                world.playSound(pos.x, pos.y, pos.z, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.0F, true);
                return TypedActionResult.success(stack);
            }
        }
        return null;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable(WATER_LEVEL_KEY, WateringCan.getWaterLevel(stack), MAX_WATER_LEVEL).formatted(Formatting.AQUA));
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return getWaterLevel(stack) < MAX_WATER_LEVEL;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        WateringCan.setWaterLevel(null, stack, 0);
        return stack;
    }

    public ItemStack getStack(int waterLevel) {
        ItemStack stack = super.getDefaultStack();
        WateringCan.setWaterLevel(null, stack, waterLevel);
        return stack;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x00AAFF;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round((WateringCan.getWaterLevel(stack) / 32.0f) * 13.0F);
    }

    public static int getWaterLevel(ItemStack stack) {
        return LegacyUtil.getOrEmptyNbt(stack).getInt("WaterLevel");
    }

    public static void setWaterLevel(World world, ItemStack stack, int waterLevel) {
        if (world == null) {
            var nbt = LegacyUtil.getOrEmptyNbt(stack);
            nbt.putInt("WaterLevel", waterLevel);
            LegacyUtil.writeNbt(stack, nbt);
            return;
        }
        var inifnity = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.INFINITY).orElse(null);
        if (
                EnchantmentHelper.getLevel(inifnity, stack) == 0
                        || waterLevel > getWaterLevel(stack)
        ) {
            var nbt = LegacyUtil.getOrEmptyNbt(stack);
            nbt.putInt("WaterLevel", waterLevel);
            LegacyUtil.writeNbt(stack, nbt);
        }
    }

    public static boolean canWater(ItemStack stack) {
        return WateringCan.getWaterLevel(stack) > 0;
    }
}