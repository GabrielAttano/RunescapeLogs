package com.rslogs.util;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;

@Slf4j
public final class ItemUtils {
    public static Item getLocalPlayerEquippedItem(Client client, EquipmentInventorySlot slot) {
        if (slot == null || client == null) {
            return null;
        }

        final ItemContainer playerEquipment = client.getItemContainer(InventoryID.WORN);

        if (playerEquipment != null) {
            return playerEquipment.getItem(slot.getSlotIdx());
        } else {
            log.warn("Invalid getPlayerEquipment call. Player equipment is Null.");
            return null;
        }
    }
}
