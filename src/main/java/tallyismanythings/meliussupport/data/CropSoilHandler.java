package tallyismanythings.meliussupport.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public record CropSoilHandler(String identifier, int regrowth, IntProvider vigorSuitable, IntProvider animaSuitable, IntProvider corpusSuitable, Vec3i idealSoil, Vec3i soilChange) {

    public static final Codec<CropSoilHandler> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("identifier").forGetter(CropSoilHandler::identifier),
            Codec.INT.fieldOf("regrowth").forGetter(CropSoilHandler::regrowth),
            IntProvider.NON_NEGATIVE_CODEC.fieldOf("vigor_suitable").forGetter(CropSoilHandler::vigorSuitable),
            IntProvider.NON_NEGATIVE_CODEC.fieldOf("anima_suitable").forGetter(CropSoilHandler::animaSuitable),
            IntProvider.NON_NEGATIVE_CODEC.fieldOf("corpus_suitable").forGetter(CropSoilHandler::corpusSuitable),
            Vec3i.CODEC.fieldOf("ideal_soil").forGetter(CropSoilHandler::idealSoil),
            Vec3i.CODEC.fieldOf("soil_change").forGetter(CropSoilHandler::soilChange)
    ).apply(i, CropSoilHandler::new));
}
