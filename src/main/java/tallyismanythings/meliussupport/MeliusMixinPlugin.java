package tallyismanythings.meliussupport;

import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

public class MeliusMixinPlugin implements IMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("Supplementaries")) {
            return FabricLoader.getInstance().isModLoaded("supplementaries");
        }
        if (mixinClassName.contains("LetsFarm")) {
            return FabricLoader.getInstance().isModLoaded("farm_and_charm");
        }
        if (mixinClassName.contains("LetsBrew")) {
            return FabricLoader.getInstance().isModLoaded("brewery");
        }
        if (mixinClassName.contains("HerbalBrew")) {
            return FabricLoader.getInstance().isModLoaded("herbalbrews");
        }
        if (mixinClassName.contains("IcyHorse")) {
            return FabricLoader.getInstance().isModLoaded("icys-better-horses");
        }

        return true;
    }
}
