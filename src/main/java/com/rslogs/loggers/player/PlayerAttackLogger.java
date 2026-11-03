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
package com.rslogs.loggers.player;

import com.rslogs.module.PluginLifecycleComponent;
import com.rslogs.util.AttackCooldown;
import com.rslogs.util.LogManager;
import com.rslogs.util.LoggableEncounter;
import com.rslogs.util.PlayerAttack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NPCManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PlayerAttackLogger implements PluginLifecycleComponent {
    private final EventBus eventBus;
    private final Client client;
    private final NPCManager npcManager;

    private final LogManager logManager;

    private AttackState attackState = AttackState.NOT_ATTACKING;

    @Override
    public void startUp() {
        eventBus.register(this);
    }

    @Override
    public void shutDown() {
        eventBus.unregister(this);
    }

    @Subscribe
    public void onGameTick(final GameTick event) {
        if (!logManager.isLogging()) {
            return;
        }

        final boolean isAttacking = PlayerAttack.isPlayerAttacking(client, npcManager);
        switch (attackState) {
            case NOT_ATTACKING:
                if (isAttacking) {
                    log.debug("onGametick NOT_ATTACKING performAttack");
                    performAttack();
                }
                break;
            case DELAYED_FIRST_TICK:
                log.debug("onGametick DELAYED_FIRST_TICK delay");
                attackState = AttackState.DELAYED;
                break;
            case DELAYED:
                if (isAttacking) {
                    log.debug("onGametick DELAYED performAttack");
                    performAttack();
                } else {
                    log.debug("onGametick DELAYED notAttacking");
                    attackState = AttackState.NOT_ATTACKING;
                }
        }
    }

    private void performAttack() {
        Actor target = client.getLocalPlayer().getInteracting();

        logManager.logPlayerAttack();

        int predictedCooldown = AttackCooldown.predictAttackCooldown(client, target, logManager.getEncounter());
        if (predictedCooldown < 0) {
            // fallback
            attackState = AttackState.DELAYED_FIRST_TICK;
        } else if (predictedCooldown == 1) {
            attackState = AttackState.DELAYED;
        } else {
            attackState = AttackState.DELAYED_FIRST_TICK;
        }
    }

    public enum AttackState
    {
        NOT_ATTACKING,
        DELAYED_FIRST_TICK,
        DELAYED,
    }
}
