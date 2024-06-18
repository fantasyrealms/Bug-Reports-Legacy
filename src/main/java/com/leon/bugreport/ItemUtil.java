package com.leon.bugreport;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class ItemUtil {

    public static boolean hasCustomModelData(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return false;
        if (XMaterial.supports(9)) {
            return itemStack.getItemMeta().hasCustomModelData();
        } else {
            return NBT.get(itemStack, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.hasTag("modelData"));
        }
    }

    public static int getCustomModelData(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return 0;
        if (XMaterial.supports(9)) {
            return itemStack.getItemMeta().getCustomModelData();
        } else {
            return NBT.get(itemStack, (Function<ReadableItemNBT, Integer>) nbt -> nbt.getInteger("modelData"));
        }
    }

}
