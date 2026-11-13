package tallyismanythings.meliussupport.data;

import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import tallyismanythings.meliussupport.MeliusSupport;

public class MeliusDataResources {
    public static final RegistryKey<Registry<CropSoilHandler>> CROP_HANDLER_KEY = RegistryKey.ofRegistry(Identifier.of("melius_farming", "crops"));

    public static void init() {
        DynamicRegistries.registerSynced(CROP_HANDLER_KEY, CropSoilHandler.CODEC);
    }
}
