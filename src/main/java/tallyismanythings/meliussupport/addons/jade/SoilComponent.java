package tallyismanythings.meliussupport.addons.jade;

import net.minecraft.block.BlockState;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import tallyismanythings.meliussupport.blocks.custom.MeliusFarmland;
import tallyismanythings.meliussupport.util.GrowthHandler;

import java.util.List;
import java.util.Optional;

public enum SoilComponent implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        net.minecraft.block.BlockState state = accessor.getBlockState();
        List<Text> types = List.of(Text.translatable("jade.melius_farming.soil_quality_none").withColor(Colors.LIGHT_RED), Text.translatable("jade.melius_farming.soil_quality_low").withColor(Colors.YELLOW), Text.translatable("jade.melius_farming.soil_quality_regular").withColor(Colors.WHITE), Text.translatable("jade.melius_farming.soil_quality_high").withColor(Colors.GREEN), Text.translatable("jade.melius_farming.soil_quality_saturated").withColor(DyeColor.LIGHT_BLUE.getSignColor()));
        int vigor = state.get(MeliusFarmland.VIGOR);
        int anima = state.get(MeliusFarmland.ANIMA);
        int corpus = state.get(MeliusFarmland.CORPUS);
        tooltip.add(Text.translatable("jade.melius_farming.vigor_state", types.get(vigor)).withColor(16752286));
        tooltip.add(Text.translatable("jade.melius_farming.anima_state", types.get(anima)).withColor(10420129));
        tooltip.add(Text.translatable("jade.melius_farming.corpus_state", types.get(corpus)).withColor(10398719));
    }

    @Override
    public Identifier getUid() {
        return MeliusFarmWaliaPlugin.REGROWTH_OPTION;
    }
}
