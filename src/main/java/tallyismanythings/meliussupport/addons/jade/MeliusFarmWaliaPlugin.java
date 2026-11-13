package tallyismanythings.meliussupport.addons.jade;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.util.Identifier;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IWailaConfig;
import tallyismanythings.meliussupport.MeliusSupport;
import tallyismanythings.meliussupport.blocks.custom.MeliusFarmland;

@WailaPlugin
public class MeliusFarmWaliaPlugin implements IWailaPlugin {

    public static Identifier REGROWTH_OPTION = MeliusSupport.id("regrowth_option");
    public static Identifier GROWTH_OPTION = MeliusSupport.id("growth_option");
    public static Identifier SOIL_OPTION = MeliusSupport.id("soil_option");

    @Override
    public void register(IWailaCommonRegistration registration) {
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(REGROWTH_OPTION, true);
        registration.addConfig(GROWTH_OPTION, true);
        registration.addConfig(SOIL_OPTION, true);

        registration.registerBlockComponent(GrowthComponent.INSTANCE, CropBlock.class);
        registration.registerBlockComponent(GrowthComponent.INSTANCE, StemBlock.class);
        registration.registerBlockComponent(GrowthComponent.INSTANCE, PitcherCropBlock.class);

        registration.registerBlockComponent(SoilComponent.INSTANCE, MeliusFarmland.class);

        registration.registerBlockComponent(RegrowComponent.INSTANCE, StemBlock.class);
        registration.registerBlockComponent(RegrowComponent.INSTANCE, CropBlock.class);

        if (FabricLoader.getInstance().isModLoaded("farm_and_charm")) {
            registration.registerBlockComponent(RegrowComponent.INSTANCE, net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock.class);
            registration.registerBlockComponent(GrowthComponent.INSTANCE, net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock.class);
        }
    }
}