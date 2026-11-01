package com.rslogs.util;

import lombok.Getter;
import net.runelite.api.Animation;
import net.runelite.api.gameval.NpcID;

import java.util.Set;

public enum NpcAnimation {
    // Check runemonk for the NPC and/or play with Runelite with the NPC option on
    MAD_ANGEL_MELEE(NpcID.MAD_ANGEL, Set.of(4589), "Melee attack"),
    MAD_ANGEL_BLAST_ATTACK(NpcID.MAD_ANGEL, Set.of(14443), "Blast attack"),
    MAD_ANGEL_SMITE_ATTACK(NpcID.MAD_ANGEL, Set.of(4590, 8543), "Smite attack");

    @Getter
    private final int npcId;
    @Getter
    private final Set<Integer> animationsId;

    @Getter
    private final String name;

    NpcAnimation(int npcId, Set<Integer> animationsId, String name) {
        this.npcId = npcId;
        this.animationsId = animationsId;
        this.name = name;
    }

    public static NpcAnimation getAnimation(int animationId) {
        for (NpcAnimation animation : NpcAnimation.values()) {
            if (animation.animationsId.contains(animationId)) {
                return animation;
            }
        }

        return null;
    }
}
