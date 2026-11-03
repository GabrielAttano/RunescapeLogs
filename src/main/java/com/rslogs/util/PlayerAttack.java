/*
 * Copyright (c) 2022, Nick Graves <https://github.com/ngraves95>
 * Copyright (c) 2024-2026, Lexer747 <https://github.com/Lexer747>
 * Copyright (c) 2024-2026, Richardant <https://github.com/Richardant>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.rslogs.util;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.SpotanimID;
import net.runelite.client.game.NPCManager;

import java.util.Arrays;
import java.util.Set;

public class PlayerAttack {
    // These animations are the ones which exceed the duration of their attack cooldown
    // so in this case DO NOT fall back the animation as it is un-reliable.
    private static final Set<PlayerAnimationData> UNRELIABLE_ANIMATIONS = new ImmutableSet.Builder<PlayerAnimationData>()
            .add(PlayerAnimationData.RANGED_BLOWPIPE).add(PlayerAnimationData.RANGED_BLAZING_BLOWPIPE)
            .add(PlayerAnimationData.MAGIC_EYE_OF_AYAK).add(PlayerAnimationData.MAGIC_EYE_OF_AYAK_SPEC).build();

    // Attacks which can happen without the player interacting with a target. The Obligator thrust can hit
    // several targets, and while using the Obligator (thrust or smash) the player is often not interacting with
    // any of them. Both animations last 3 ticks, never longer than their cooldown (5t thrust, 3t smash), so they
    // can't be mistaken for the end of the previous attack.
    private static final Set<PlayerAnimationData> NO_TARGET_ATTACKS = new ImmutableSet.Builder<PlayerAnimationData>()
            .add(PlayerAnimationData.MELEE_OBLIGATOR).add(PlayerAnimationData.MELEE_OBLIGATOR_FEMALE)
            .add(PlayerAnimationData.MELEE_OBLIGATOR_MULTIHIT).add(PlayerAnimationData.MELEE_OBLIGATOR_MULTIHIT_FEMALE).build();

    // Combat Dummy
    public static final Set<Integer> COMBAT_DUMMY = new ImmutableSet.Builder<Integer>()
            .add(NpcID.POH_COMBAT_DUMMY_UPGRADED_UNDEADSLAYER_NPC, NpcID.POH_COMBAT_DUMMY_UPGRADED_ETHER_NPC,
                    NpcID.POH_COMBAT_DUMMY_UPGRADED_KQ_NPC, NpcID.POH_COMBAT_DUMMY_UPGRADED_KURASK_NPC,
                    NpcID.POH_COMBAT_DUMMY_UPGRADED_VAMPIRE_NPC, NpcID.POH_COMBAT_DUMMY_UPGRADED_VORKATH_NPC,
                    NpcID.POH_COMBAT_DUMMY_NPC, NpcID.POH_COMBAT_DUMMY_UNDEADSLAYER_NPC,
                    NpcID.POH_COMBAT_DUMMY_UPGRADED_NPC)
            .build();

    // Combat Dummy + Nightmare Pillars
    private static final Set<Integer> SPECIAL_NPCS = new ImmutableSet.Builder<Integer>()
            .add(NpcID.NIGHTMARE_TOTEM_1_READY, NpcID.NIGHTMARE_TOTEM_2_READY,
                    NpcID.NIGHTMARE_TOTEM_3_READY, NpcID.NIGHTMARE_TOTEM_4_READY)
            .addAll(COMBAT_DUMMY)
            .build();

    public static boolean isPlayerAttacking(final Client client, final NPCManager npcManager) {
        final Player localPlayer = client.getLocalPlayer();
        final int animationId = localPlayer.getAnimation();

        // Most weapons overwrite blockList animations, apart from the zorya tome, how odd.
        if (PlayerAnimationData.isBlockListAnimation(animationId) && !isZoryaAttack(localPlayer)) {
            return false;
        }

        final PlayerAnimationData playerAnimationData = PlayerAnimationData.fromId(animationId);

        // Not walking is either ANY player animation or the edge cases which don't trigger an animation,
        // e.g Salamander.
        final boolean notWalking = animationId != -1 || isSpotAnimAttack(localPlayer);

        // Testing if we are attacking by checking the target is more future proof to new weapons which don't
        // need custom code and the weapon stats are enough.
        final Actor target = localPlayer.getInteracting();

        if ((target instanceof NPC))
        {
            final NPC targetNpc = (NPC) target;

            final boolean isNpcAttackable = Arrays.asList(targetNpc.getComposition().getActions()).contains("Attack");
            final Integer npcHealth = npcManager.getHealth(targetNpc.getId());
            final boolean npcHasHealthAndLevel = npcHealth != null && npcHealth > 0 && target.getCombatLevel() > 0;

            final boolean isAttackingNPC = npcHasHealthAndLevel || SPECIAL_NPCS.contains(targetNpc.getId())  || isNpcAttackable;

            // just having a target is not enough the player may be out of range, we must wait for any
            // animation which isn't running/walking/etc
            return isAttackingNPC && notWalking;
        }

        if ((target instanceof Player))
        {
            return notWalking;
        }

        if (target == null)
        {
            // Not attacking anything, unless it's an attack which doesn't need a target
            return NO_TARGET_ATTACKS.contains(playerAnimationData);
        }

        // Do not use any animations from this set
        if (UNRELIABLE_ANIMATIONS.contains(playerAnimationData))
        {
            return false;
        }

        // fall back to animations.
        return playerAnimationData != null;
    }

    private static boolean isZoryaAttack(final Player lp) {
        return lp.hasSpotAnim(SpotanimID.VFX_ZORYA_TOME_STANDARD_SPOTANIM)
                || lp.hasSpotAnim(SpotanimID.VFX_ZORYA_TOME_STANDARD_SPOTANIM_FEMALE)
                || isZoryaSpecAttack(lp);
    }

    public static boolean isZoryaSpecAttack(final Player lp) {
        return lp.hasSpotAnim(SpotanimID.VFX_ZORYA_TOME_SPEC_SPOTANIM)
                || lp.hasSpotAnim(SpotanimID.VFX_ZORYA_TOME_SPEC_SPOTANIM_FEMALE);
    }

    private static boolean isSpotAnimAttack(final Player lp)
    {
        return isSalamanderAttack(lp) || isZoryaAttack(lp);
    }

    private static boolean isSalamanderAttack(final Player lp)
    {
        return lp.hasSpotAnim(SpotanimID.FIREBREATH);
    }
}

