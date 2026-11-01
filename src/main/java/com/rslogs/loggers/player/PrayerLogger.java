package com.rslogs.loggers.player;

import com.google.common.collect.ImmutableSet;
import com.rslogs.module.PluginLifecycleComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Prayer;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PrayerLogger implements PluginLifecycleComponent {
    private final EventBus eventBus;
    private final Client client;

    private static final ImmutableSet<Prayer> TRACKED_PRAYERS = ImmutableSet.of(
            Prayer.BURST_OF_STRENGTH,
            Prayer.CLARITY_OF_THOUGHT,
            Prayer.SHARP_EYE,
            Prayer.MYSTIC_WILL,
            Prayer.SUPERHUMAN_STRENGTH,
            Prayer.IMPROVED_REFLEXES,
            Prayer.HAWK_EYE,
            Prayer.MYSTIC_LORE,
            Prayer.ULTIMATE_STRENGTH,
            Prayer.INCREDIBLE_REFLEXES,
            Prayer.EAGLE_EYE,
            Prayer.DEADEYE,
            Prayer.MYSTIC_MIGHT,
            Prayer.MYSTIC_VIGOUR,
            Prayer.CHIVALRY,
            Prayer.PIETY,
            Prayer.ZEAL,
            Prayer.RIGOUR,
            Prayer.AUGURY,
            Prayer.PROTECT_FROM_MELEE,
            Prayer.PROTECT_FROM_MAGIC,
            Prayer.PROTECT_FROM_MISSILES
    );

    @Override
    public void startUp() {
        eventBus.register(this);
    }

    @Override
    public void shutDown() {
        eventBus.unregister(this);
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        final int tickCount = client.getTickCount();

        processPrayers(tickCount);
    }

    private void processPrayers(int tickCount) {
        Collection<Prayer> activePrayers = getActivePrayers();
    }

    private Collection<Prayer> getActivePrayers() {
        final Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null) {
            return Collections.emptyList();
        }

        Set<Prayer> activePrayers = new HashSet<>();
        for (Prayer prayer : TRACKED_PRAYERS) {
            if (client.getServerVarbitValue(prayer.getVarbit()) == 1) {
                activePrayers.add(prayer);
            }
        }

        return activePrayers;
    }

}
