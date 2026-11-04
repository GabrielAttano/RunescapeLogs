package com.rslogs.loggers.boss;

import com.rslogs.module.PluginLifecycleComponent;
import com.rslogs.loggers.LogManager;
import com.rslogs.util.LoggableEncounter;
import com.rslogs.util.NpcAnimationData;
import com.rslogs.util.NpcUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ScurriusLogger implements PluginLifecycleComponent {
    private final EventBus eventBus;
    private final Client client;

    private final LogManager logManager;

    // private final int GROUP_SCURRIUS_ID = NpcID.RAT_BOSS_NORMAL;
    private final int SOLO_SCURRIUS_ID = NpcID.RAT_BOSS_INSTANCE;

    @Override
    public void startUp() {
        eventBus.register(this);
    }

    @Override
    public void shutDown() {
        eventBus.unregister(this);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if (NpcUtils.isNpc(event.getNpc(), SOLO_SCURRIUS_ID) && !logManager.isLogging()) {
            logManager.startLogging(LoggableEncounter.SCURRIUS);
            return;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        if (!logManager.isLogging()) {
            return;
        }

        if (NpcUtils.isNpc(event.getNpc(), SOLO_SCURRIUS_ID)) {
            logManager.endLogging();
        }
    }

    @Subscribe
    public void onActorDeath(ActorDeath event) {
        if (!logManager.isLogging()) {
            return;
        }

        if (NpcUtils.isNpc(event.getActor(), SOLO_SCURRIUS_ID)) {
            logManager.endLogging();
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (!logManager.isLogging()) {
            return;
        }

        Actor actor = event.getActor();
        if (NpcUtils.isNpc(actor, SOLO_SCURRIUS_ID)) {
            NpcAnimationData npcAnimationData = NpcAnimationData.getAnimation(actor.getAnimation());
            if (npcAnimationData != null) {
                logManager.logNpcAttack(SOLO_SCURRIUS_ID, npcAnimationData.getName());
            }
        }
    }
}
