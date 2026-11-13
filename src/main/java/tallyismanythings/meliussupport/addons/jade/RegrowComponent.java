package tallyismanythings.meliussupport.addons.jade;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import tallyismanythings.meliussupport.util.GrowthHandler;

import java.util.Optional;

public enum RegrowComponent implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        Optional<Integer> prop = accessor.getBlockState().getOrEmpty(GrowthHandler.REGROWTH);
        if (prop.isPresent() && prop.get() > 0) {
            int days = GrowthHandler.getFullRegrowTime(accessor.getBlockState(), accessor.getLevel(), accessor.getPosition());
            if (days > 0) {
                tooltip.add(Text.translatable("jade.melius_farming.regrowth", days));
            } else if (days == -99) {
                tooltip.add(Text.translatable("jade.melius_farming.no_regrowth", days).withColor(Colors.LIGHT_RED));
            }
        }
    }

    @Override
    public Identifier getUid() {
        return MeliusFarmWaliaPlugin.REGROWTH_OPTION;
    }
}
