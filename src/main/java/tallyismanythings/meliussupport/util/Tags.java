package tallyismanythings.meliussupport.util;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import tallyismanythings.meliussupport.MeliusSupport;

public class Tags {
    public static final TagKey<Block> CROPS = TagKey.of(RegistryKeys.BLOCK, MeliusSupport.id("crops_special"));
    public static final TagKey<Block> WATER_PROVIDER = TagKey.of(RegistryKeys.BLOCK, MeliusSupport.id("water_provider"));
}
