package com.github.alexnijjar.the_extractinator.compat.rei.util;

import com.github.alexnijjar.the_extractinator.TheExtractinator;
import com.github.alexnijjar.the_extractinator.client.TheExtractinatorClient;
import com.github.alexnijjar.the_extractinator.data.LootSlot;
import com.github.alexnijjar.the_extractinator.data.LootTable;
import com.github.alexnijjar.the_extractinator.data.SupportedBlock;
import com.github.alexnijjar.the_extractinator.util.TEUtils;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class REILootDisplay {

    public static List<LootSlot> getOutput(SupportedBlock supportedBlock) {

        List<LootSlot> loot = new ArrayList<>();
        List<LootSlot> lootTableSlots = new ArrayList<>();

        for (LootTable entry : TheExtractinatorClient.lootTables) {
            if (TEUtils.modLoaded(entry.namespace) && supportedBlock.tier.equals(entry.tier))
                lootTableSlots.addAll(entry.slots);
        }

        if (lootTableSlots.isEmpty()) {
            TheExtractinator.LOGGER.error("Error loading REI loot table for: \"" + supportedBlock.id.toString() + "\"");
            return loot;
        }

        loot.addAll(lootTableSlots);

        // Remove disabled drops.
        for (Identifier drop : supportedBlock.disabledDrops)
            loot.removeIf(slot -> slot.id.equals(drop));

        // Add additional drops. Note: disabled drops does not affect this.
        for (LootSlot slot : supportedBlock.additionalDrops)
            if (!loot.contains(slot)) loot.add(slot);

        // Sort alphabetically.
        loot.sort(Comparator.comparing(s -> s.id.getPath()));

        // Then sort by rarity.
        loot.sort(Comparator.comparing(s -> s.rarity.ordinal()));

        return loot;
    }
}