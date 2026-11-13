package tallyismanythings.meliussupport.util;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import tallyismanythings.meliussupport.MeliusSupport;

import java.util.List;
import java.util.Map;

public class MeliusConfig {
    public static ConfigClassHandler<MeliusConfig> HANDLER = ConfigClassHandler.createBuilder(MeliusConfig.class)
            .id(MeliusSupport.id("melius_farming_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("melius_farming_config.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry(comment = "The time of day daily reset logic will occur (within the refresh rate ticks)")
    public int dailyTimeMin = 0;

    @SerialEntry(comment = "The rate at which farmland checks itself in ticks, higher is more performant but may take longer to adjust per day")
    public int farmRefreshRate = 20;

    @SerialEntry(comment = "For mods that have large variants of crops, what is the base chance that crops will grow into them? 0.01 being a one percent chance.")
    public float bigCropChance = 0.01f;

    @SerialEntry(comment = "For mods that have huge variants of crops, what is the base chance that crops will grow into them? 0.01 being a one percent chance.")
    public float hugeCropChance = 0.001f;
}