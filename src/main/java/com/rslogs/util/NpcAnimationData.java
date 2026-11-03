package com.rslogs.util;

import lombok.Getter;
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.NpcID;

import java.util.Set;

public enum NpcAnimationData {
    // Check runemonk for the NPC and/or play with Runelite with the NPC option on
    MAD_ANGEL_MELEE(NpcID.MAD_ANGEL, Set.of(AnimationID.NPC_MAD_ANGEL_ATTACK_MELEE), "Melee attack"),
    MAD_ANGEL_BLAST_ATTACK(NpcID.MAD_ANGEL, Set.of(AnimationID.NPC_MAD_ANGEL_ATTACK_BOMB_THROW), "Blast attack"),
    MAD_ANGEL_SMITE_ATTACK(NpcID.MAD_ANGEL, Set.of(AnimationID.NPC_MAD_ANGEL_ATTACK_SMITE, AnimationID.NPC_MAD_ANGEL_ATTACK_SMITE_ENRAGED), "Smite attack");

    @Getter
    private final int npcId;
    @Getter
    private final Set<Integer> animationsId;

    @Getter
    private final String name;

    NpcAnimationData(int npcId, Set<Integer> animationsId, String name) {
        this.npcId = npcId;
        this.animationsId = animationsId;
        this.name = name;
    }

    public static NpcAnimationData getAnimation(int animationId) {
        for (NpcAnimationData animation : NpcAnimationData.values()) {
            if (animation.animationsId.contains(animationId)) {
                return animation;
            }
        }

        return null;
    }
}
