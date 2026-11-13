package tallyismanythings.meliussupport.blocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import tallyismanythings.meliussupport.MeliusSupport;
import tallyismanythings.meliussupport.blocks.custom.MeliusFarmland;

public class MeliusBlocks {
    public static final Block FARMLAND = registerWithSimpleItem("farmland", new MeliusFarmland(AbstractBlock.Settings.copy(Blocks.FARMLAND)));

    public static <T extends Block> T registerBlock(String name, T block) {
        return Registry.register(Registries.BLOCK, MeliusSupport.id(name), block);
    }
    public static <T extends Block> T registerWithSimpleItem(String name, T block) {
        Registry.register(Registries.BLOCK, MeliusSupport.id(name), block);
        Registry.register(Registries.ITEM, MeliusSupport.id(name), new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void initialize() {}
}
