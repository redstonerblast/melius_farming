package tallyismanythings.meliussupport.addons.jade;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.CropBlock;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import tallyismanythings.meliussupport.addons.letsdofarm.LetsFarmGrowthHandler;
import tallyismanythings.meliussupport.util.GrowthHandler;

import java.util.Optional;

public enum GrowthComponent implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        int days = 0;
        float mult = GrowthHandler.getSoilMult(accessor.getLevel(), accessor.getBlockState(), accessor.getPosition());
        tooltip.add(Text.translatable("jade.melius_farming.soil_quality").append(mult == 1.0 ? Text.translatable("jade.melius_farming.quality_suitable").withColor(Colors.GREEN) : mult == 1.5 ? Text.translatable("jade.melius_farming.quality_ideal").withColor(Colors.YELLOW) : Text.translatable("jade.melius_farming.quality_poor").withColor(DyeColor.MAGENTA.getSignColor())));
        if (accessor.getBlockState().getOrEmpty(GrowthHandler.REGROWTH).isPresent() && accessor.getBlockState().get(GrowthHandler.REGROWTH) > 0) {
            return;
        }
        if (accessor.getBlock() instanceof CropBlock) {
            days = GrowthHandler.getFullGrowthTimeCrop(accessor.getBlockState(), accessor.getLevel(), accessor.getPosition());
        } else if (accessor.getBlock() instanceof PitcherCropBlock) {
            days = GrowthHandler.getFullGrowthTime(accessor.getBlockState(), accessor.getLevel(), accessor.getPosition(), 4, PitcherCropBlock.AGE);
        } else if (accessor.getBlock() instanceof StemBlock) {
            days = GrowthHandler.getFullGrowthTime(accessor.getBlockState(), accessor.getLevel(), accessor.getPosition(), 7, StemBlock.AGE);
        } else {
            if (FabricLoader.getInstance().isModLoaded("farm_and_charm")) {
                if (accessor.getBlock() instanceof net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock) {
                    days = LetsFarmGrowthHandler.getDays(accessor);
                }
            }
        }
        if (days > 0) {
            tooltip.add(Text.translatable("jade.melius_farming.growth", days));
        } else if (days == -99) {
            tooltip.add(Text.translatable("jade.melius_farming.no_growth", days).withColor(Colors.LIGHT_RED));
        }
    }

    @Override
    public Identifier getUid() {
        return MeliusFarmWaliaPlugin.GROWTH_OPTION;
    }
}
