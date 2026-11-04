package com.rslogs.loggers;

import com.rslogs.module.PluginLifecycleComponent;
import com.rslogs.util.LoggableEncounter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.gameval.InventoryID;
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
    private LoggableEncounter encounter;
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

    private void reset() {
        this.encounterStartTick = 0;
        this.encounterEndTick = 0;
        this.isLogging = false;
        this.encounter = null;
    }

    public void startLogging(LoggableEncounter loggableEncounter) {
        reset();
        this.isLogging = true;
        this.encounterStartTick = client.getTickCount();
        this.encounter = loggableEncounter;
        log.debug("====> Start logging. encounter: {}", encounter);
    }

    public void endLogging() {
        encounterEndTick = client.getTickCount();
        log.debug("====> End logging. startTick: {}, endTick: {}", encounterStartTick, encounterEndTick);
        log.debug("Total: {}", encounterEndTick - encounterStartTick);
        reset();
    }

    public void logNpcAttack(int npcId, String attackName) {
        int tick = client.getTickCount();
        log.debug("== npcAttack ==");
        log.debug("tick: {} - attack: {}", encounterRelativeTick(tick), attackName);
    }

    public void logNpcSkillshot(int npcId, String attackName, LocalPoint point) {
        int tick = client.getTickCount();
        log.debug("== npcSkillshot ==");
        log.debug("tick: {} - attack: {} - posX: {}, posY: {}", encounterRelativeTick(tick), attackName, point.getX(), point.getY());
    }

    public void logPlayerAttack() {
        int tick = client.getTickCount();

        log.debug("== playerAttack ==");
        final ItemContainer playerEquipment = client.getItemContainer(InventoryID.WORN);
        if (playerEquipment != null) {
            final Item weapon = playerEquipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
            final String weaponName = weapon != null
                    ? client.getItemDefinition(weapon.getId()).getName()
                    : "";

            final Item shield = playerEquipment.getItem(EquipmentInventorySlot.SHIELD.getSlotIdx());
            final String shieldName = shield != null
                    ? client.getItemDefinition(shield.getId()).getName()
                    : "";

            log.debug("tick: {}, weapon: {}, shield: {}", encounterRelativeTick(tick), weaponName, shieldName);
        } else {
            log.warn("Fallback - unable to get playerEquipment.");
            log.debug("tick: {}", encounterRelativeTick(tick));
        }
    }

    private int encounterRelativeTick(int clientTick) {
        if (clientTick < encounterStartTick) {
            return clientTick;
        }

        return clientTick - encounterStartTick;
    }
}
