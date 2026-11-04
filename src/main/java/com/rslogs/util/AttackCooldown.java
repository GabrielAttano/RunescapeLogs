package com.rslogs.util;

import com.google.common.collect.ImmutableSet;
import com.rslogs.util.data.LoggableEncounter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;

import java.util.Set;

@Slf4j
public final class AttackCooldown {
    private static final ImmutableSet<LoggableEncounter> specialEncounters = ImmutableSet.of(
            LoggableEncounter.SCURRIUS
    );

    public static int predictAttackCooldown(Client client, Actor target, LoggableEncounter encounter) {
        if (client == null) {
            return -1;
        }

        if (!(target instanceof NPC)) {
            return 1;
        }

        NPC npc = (NPC) target;
        boolean isSpecialEncounter = specialEncounters.contains(encounter);
        return isSpecialEncounter
                ? predictSpecialEncounterAttackCooldown(client, npc, encounter)
                : predictNormalAttackCooldown(client, npc);
    }

    private static int predictNormalAttackCooldown(Client client, NPC target) {
        // Defaulting to two as of right now
        // TODO: Check for animations that lasts longer than the default cooldown
        // It seems like ice barrage causes this.
        return 2;
    }

    private static int predictSpecialEncounterAttackCooldown(Client client, NPC target, LoggableEncounter encounter) {
        switch (encounter) {
            case SCURRIUS:
                return scurriusAttackCooldown(client, target);
            default:
                log.warn("Predict special encounter cooldown fallback to normalAttack");
                return predictNormalAttackCooldown(client, target);
        }
    }

    private static int scurriusAttackCooldown(Client client, NPC target) {
        final Set<Integer> ratbaneWeapons = Set.of(ItemID.RAT_BONE_MACE, ItemID.RAT_BONE_BOW, ItemID.RAT_BONE_STAFF);
        final int scurriusSummonId = NpcID.RAT_BOSS_GIANT_RAT;

        // No attack cooldown if the target is scurrius summon
        // and the player is wielding a ratbane weapon
        // Might need to check how it works with staff
        if (target.getId() == scurriusSummonId) {
            Item weapon = ItemUtils.getLocalPlayerEquippedItem(client, EquipmentInventorySlot.WEAPON);
            if (ratbaneWeapons.contains(weapon.getId())) {
                return 1;
            }
        }

        return predictNormalAttackCooldown(client, target);
    }
}
