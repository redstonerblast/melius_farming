package tallyismanythings.meliussupport.mixin.accessors;

import net.minecraft.block.Block;
import net.minecraft.block.StemBlock;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StemBlock.class)
public interface StemBlockAccessor {
    @Accessor("gourdBlock")
    RegistryKey<Block> meliusSupport$gourdBlock();
    @Accessor("attachedStemBlock")
    RegistryKey<Block> meliusSupport$attachedStemBlock();
}
