package com.rslogs.util;

import com.rslogs.module.PluginLifecycleComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class LogManager implements PluginLifecycleComponent {
    private final EventBus eventBus;
    private final Client client;

    @Getter
    private boolean isLogging = false;
    @Getter
    private int encounterStartTick;
    @Getter
    private int encounterEndTick;

    @Override
    public void startUp() {
        eventBus.register(this);
    }

    @Override
    public void shutDown() {
        eventBus.unregister(this);
    }

    public void startLogging() {
        this.isLogging = true;
        encounterStartTick = client.getTickCount();
        log.debug("====> Start logging. startTick: {}", encounterStartTick);
    }

    public void endLogging() {
        this.isLogging = false;
        encounterEndTick = client.getTickCount();
        log.debug("====> End logging. startTick: {}, endTick: {}", encounterStartTick, encounterEndTick);
        log.debug("Total: {}", encounterEndTick - encounterStartTick);
    }

    public void logNpcAttack(int npcId, String attackName) {
        int tick = client.getTickCount();
        log.debug("== npcAttack ==");
        log.debug("tick: {} - attack: {}", tick, attackName);
    }
}
