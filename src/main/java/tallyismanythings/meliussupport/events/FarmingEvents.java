package tallyismanythings.meliussupport.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import tallyismanythings.meliussupport.blocks.MeliusBlocks;

public class FarmingEvents {
    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, result) -> {
            if (player.getStackInHand(hand).getItem() instanceof HoeItem && world.getBlockState(result.getBlockPos()).isIn(BlockTags.DIRT)) {
                world.playSound(player, result.getBlockPos(), SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.setBlockState(result.getBlockPos(), MeliusBlocks.FARMLAND.getDefaultState(), 2);
                world.scheduleBlockTick(result.getBlockPos(), MeliusBlocks.FARMLAND, 10);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
}
