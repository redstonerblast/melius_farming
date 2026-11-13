package tallyismanythings.meliussupport.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.ColorHelper;
import tallyismanythings.meliussupport.blocks.MeliusBlocks;
import tallyismanythings.meliussupport.blocks.custom.MeliusFarmland;

public class MeliusSupportClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(MeliusBlocks.FARMLAND, RenderLayer.getTranslucent());

		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> Integer.decode(String.format("#%02X%02X%02X", state.get(MeliusFarmland.VIGOR) * 50 + 27, state.get(MeliusFarmland.ANIMA) * 40 + 47, state.get(MeliusFarmland.CORPUS) * 40 + 47)), MeliusBlocks.FARMLAND);
	}
}