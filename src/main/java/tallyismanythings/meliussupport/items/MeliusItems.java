package tallyismanythings.meliussupport.items;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import tallyismanythings.meliussupport.MeliusSupport;
import tallyismanythings.meliussupport.items.custom.WateringCan;

public class MeliusItems {
    public static final Item WATERING_CAN = registerItem("watering_can", new WateringCan(new Item.Settings().maxCount(1)));

    public static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, MeliusSupport.id(name), item);
    }



    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.add(WATERING_CAN);
        });
    }
}
