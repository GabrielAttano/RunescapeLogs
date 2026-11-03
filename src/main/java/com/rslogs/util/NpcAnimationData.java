package com.rslogs.util;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.NpcID;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Set;

public enum NpcAnimationData {
    // Check runemonk for the NPC and/or play with Runelite with the NPC option on
    // Runemonk: https://runemonk.com/

    // === Mad Angel ===
    MAD_ANGEL_MELEE(NpcID.MAD_ANGEL, Set.of(AnimationID.NPC_MAD_ANGEL_ATTACK_MELEE), "Melee attack"),
    MAD_ANGEL_BLAST_ATTACK(NpcID.MAD_ANGEL, Set.of(AnimationID.NPC_MAD_ANGEL_ATTACK_BOMB_THROW), "Blast attack"),
    MAD_ANGEL_SMITE_ATTACK(NpcID.MAD_ANGEL, Set.of(AnimationID.NPC_MAD_ANGEL_ATTACK_SMITE, AnimationID.NPC_MAD_ANGEL_ATTACK_SMITE_ENRAGED), "Smite attack"),

    // === Scurrius ===
    SCURRIUS_MELEE(NpcID.RAT_BOSS_INSTANCE,
            Set.of(AnimationID.NPC_RAT_BOSS_ATTACK_MELEE_01, AnimationID.NPC_RAT_BOSS_ATTACK_MELEE_02),
            "Melee attack"),
    SCURRIUS_RANGED(NpcID.RAT_BOSS_INSTANCE,
            Set.of(AnimationID.NPC_RAT_BOSS_ATTACK_RANGED_01, AnimationID.NPC_RAT_BOSS_FEEDING_ATTACK_RANGED_01),
            "Ranged attack"),
    SCURRIUS_MAGIC(NpcID.RAT_BOSS_INSTANCE,
            Set.of(AnimationID.NPC_RAT_BOSS_ATTACK_MAGIC_01, AnimationID.NPC_RAT_BOSS_FEEDING_ATTACK_MAGIC_01),
            "Magic attack"),
    SCURRIUS_STOMP(NpcID.RAT_BOSS_INSTANCE,
            Set.of(AnimationID.NPC_RAT_BOSS_ATTACK_STOMP_01, AnimationID.NPC_RAT_BOSS_FEEDING_ATTACK_STOMP_01),
            "Stomp"),
    SCURRIUS_SUMMON(NpcID.RAT_BOSS_INSTANCE,
            Set.of(AnimationID.NPC_RAT_BOSS_ATTACK_SUMMON_01, AnimationID.NPC_RAT_BOSS_ATTACK_SUMMON_02,
                    AnimationID.NPC_RAT_BOSS_FEEDING_ATTACK_SUMMON_01, AnimationID.NPC_RAT_BOSS_FEEDING_ATTACK_SUMMON_02),
            "Summon");

    @Getter
    private final int npcId;
    @Getter
    private final Set<Integer> animationsId;
    @Getter
    private final String name;

    private static final Map<Integer, NpcAnimationData> DATA;

    static {
        ImmutableMap.Builder<Integer, NpcAnimationData> dataBuilder = new ImmutableMap.Builder<>();

        for (NpcAnimationData npcAnimation : values()) {
            Set<Integer> animationIds = npcAnimation.getAnimationsId();
            if (animationIds == null || animationIds.isEmpty()) {
                throw new InvalidParameterException("Npc animation data should have at least one animationId");
            }

            for (int animationId : animationIds) {
                dataBuilder.put(animationId, npcAnimation);
            }
        }

        DATA = dataBuilder.build();
    }

    NpcAnimationData(int npcId, Set<Integer> animationsId, String name) {
        this.npcId = npcId;
        this.animationsId = animationsId;
        this.name = name;
    }

    public static NpcAnimationData getAnimation(int animationId) {
        return DATA.get(animationId);
    }
}
