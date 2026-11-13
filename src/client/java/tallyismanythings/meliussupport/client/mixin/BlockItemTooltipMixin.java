package tallyismanythings.meliussupport.client.mixin;

import io.github.lucaargolo.seasons.resources.CropConfigs;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tallyismanythings.meliussupport.data.CropSoilHandler;
import tallyismanythings.meliussupport.data.MeliusDataResources;

import java.util.List;
import java.util.Optional;

@Mixin(BlockItem.class)
public class BlockItemTooltipMixin {
    @Shadow
    public Block getBlock() {
        return null;
    }

    @Inject(method = "appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/item/tooltip/TooltipType;)V", at = @At("TAIL"))
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        if (this.getBlock().getDefaultState().isIn(BlockTags.CROPS)) {
            MinecraftClient client = MinecraftClient.getInstance();
            long handle = client.getWindow().getHandle();
            KeyBinding sprintKey = client.options.sprintKey;
            InputUtil.Key boundKey = sprintKey.boundKey;
            boolean sprint = false;
            if(boundKey.getCategory() == InputUtil.Type.MOUSE) {
                sprint = GLFW.glfwGetMouseButton(handle, boundKey.getCode()) == 1;
            }else if(boundKey.getCategory() == InputUtil.Type.KEYSYM) {
                sprint = GLFW.glfwGetKey(handle, boundKey.getCode()) == 1;
            }
            if (MinecraftClient.getInstance().world != null && sprint) {
                DynamicRegistryManager dynamicRegistryManager = MinecraftClient.getInstance().world.getRegistryManager();
                Optional<CropSoilHandler> soilHandle = dynamicRegistryManager.get(MeliusDataResources.CROP_HANDLER_KEY).stream().filter((a) -> Identifier.of(a.identifier()).equals(Registries.BLOCK.getId(getBlock()))).findFirst();
                if (soilHandle.isEmpty()) {
                    return;
                }
                CropSoilHandler soilHandler = soilHandle.get();
                List<Text> types = List.of(Text.translatable("jade.melius_farming.soil_quality_none").setStyle(Style.EMPTY.withBold(false)).withColor(Colors.LIGHT_RED),
                        Text.translatable("jade.melius_farming.soil_quality_low").setStyle(Style.EMPTY.withBold(false)).withColor(Colors.YELLOW),
                        Text.translatable("jade.melius_farming.soil_quality_regular").setStyle(Style.EMPTY.withBold(false)).withColor(Colors.WHITE),
                        Text.translatable("jade.melius_farming.soil_quality_high").setStyle(Style.EMPTY.withBold(false)).withColor(Colors.GREEN),
                        Text.translatable("jade.melius_farming.soil_quality_saturated").setStyle(Style.EMPTY.withBold(false)).withColor(DyeColor.LIGHT_BLUE.getSignColor()));
                tooltip.add(Text.translatable("item.melius_farming.crop.suitable").withColor(DyeColor.ORANGE.getSignColor()).setStyle(Style.EMPTY.withBold(true).withUnderline(true)));
                tooltip.add(Text.translatable("item.melius_farming.vigor_suitable", types.get(soilHandler.vigorSuitable().getMin()), types.get(soilHandler.vigorSuitable().getMax())).setStyle(Style.EMPTY.withBold(true)).withColor(16752286));
                tooltip.add(Text.translatable("item.melius_farming.anima_suitable", types.get(soilHandler.animaSuitable().getMin()), types.get(soilHandler.animaSuitable().getMax())).setStyle(Style.EMPTY.withBold(true)).withColor(10420129));
                tooltip.add(Text.translatable("item.melius_farming.corpus_suitable", types.get(soilHandler.corpusSuitable().getMin()), types.get(soilHandler.corpusSuitable().getMax())).setStyle(Style.EMPTY.withBold(true).withColor(10398719)));

                Vec3i change = soilHandler.soilChange();
                tooltip.add(Text.literal(""));
                tooltip.add(Text.translatable("item.melius_farming.ideal", types.get(soilHandler.idealSoil().getX()).copy().withColor(16752286), types.get(soilHandler.idealSoil().getY()).copy().withColor(10420129), types.get(soilHandler.idealSoil().getZ()).copy().withColor(10398719)).setStyle(Style.EMPTY.withBold(true)));
                tooltip.add(Text.translatable("item.melius_farming.change", Text.literal((change.getX() > 0 ? "+" : "") + change.getX()).withColor(16752286), Text.literal((change.getY() > 0 ? "+" : "") + change.getY()).withColor(10420129), Text.literal((change.getZ() > 0 ? "+" : "") + change.getZ()).withColor(10398719)).setStyle(Style.EMPTY.withBold(true)));

            } else {
                tooltip.add(Text.translatable("tooltip.meliusfarming.show_more", new Object[]{sprintKey.getBoundKeyLocalizedText().copy().formatted(Formatting.GREEN)}).formatted(Formatting.GRAY));
            }
        }
    }
}
