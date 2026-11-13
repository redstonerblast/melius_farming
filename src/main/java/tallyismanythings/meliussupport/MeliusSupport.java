package tallyismanythings.meliussupport;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tallyismanythings.meliussupport.blocks.MeliusBlocks;
import tallyismanythings.meliussupport.data.MeliusDataResources;
import tallyismanythings.meliussupport.events.EventHandler;
import tallyismanythings.meliussupport.items.MeliusItems;
import tallyismanythings.meliussupport.util.MeliusConfig;

import java.io.InputStream;
import java.util.List;

public class MeliusSupport implements ModInitializer, PreLaunchEntrypoint {
	public static final String MOD_ID = "melius_farming";

	public static final AttachmentType<Integer> PERSISTENT_DAY_TRACKER = AttachmentRegistry.createPersistent(
			Identifier.of(MOD_ID, "persistent_day_tracker"),
			Codec.INT
	);

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static MeliusConfig Config;

	@Override
	public void onPreLaunch() {
		MeliusConfig.HANDLER.load();
		MeliusConfig.HANDLER.save();
		Config = MeliusConfig.HANDLER.instance();
	}

	@Override
	public void onInitialize() {
		MeliusDataResources.init();

		MeliusBlocks.initialize();
		MeliusItems.initialize();

		EventHandler.onInitialize();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
