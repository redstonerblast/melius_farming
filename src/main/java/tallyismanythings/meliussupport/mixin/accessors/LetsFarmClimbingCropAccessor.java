package tallyismanythings.meliussupport.mixin.accessors;

import net.minecraft.state.property.IntProperty;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClimbingCropBlock.class)
public interface LetsFarmClimbingCropAccessor {
    @Invoker("getAgeProperty")
    IntProperty meliusSupport$getAgeProperty();
    @Invoker("getMaxAge")
    int meliusSupport$getMaxAge();
}
