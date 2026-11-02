package com.rslogs.loggers.boss;

import com.rslogs.module.PluginLifecycleComponent;
import com.rslogs.util.LogManager;
import com.rslogs.util.LoggableEncounter;
import com.rslogs.util.NpcAnimation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.events.*;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.SpotanimID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MadAngelLogger implements PluginLifecycleComponent {
    private final EventBus eventBus;
    private final Client client;

    private final LogManager logManager;

    private final int BLAST_ATTACK_SHADOW = SpotanimID.GARGBOSS_DEBRIS_SHADOW_150;
    private final int BLAST_ATTACK_EXPLOSION_ID = SpotanimID.VFX_MAD_ANGEL_ATTACK_BOMB_THROW_IMPACT_01;
    private final int SWIPE_ATTACK_SMOKE_ID = SpotanimID.ZEBAK_ROAR_WAVE_DUST;

    private int lastSwipeTick;
    private int lastExplosionTick;

    @Override
    public void startUp() {
        eventBus.register(this);
        reset();
    }

    @Override
    public void shutDown() {
        eventBus.unregister(this);
    }

    private void reset() {
        lastSwipeTick = 0;
        lastExplosionTick = 0;
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        if (isMadAngel(event.getNpc()) && !logManager.isLogging()) {
            logManager.startLogging(LoggableEncounter.MAD_ANGEL);
            return;
        }

        if (isMadAngelDead(event.getNpc())) {
            logManager.endLogging();
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        if (!logManager.isLogging()) {
            return;
    }

        if (isMadAngel(event.getNpc())) {
            logManager.endLogging();
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        if (!logManager.isLogging()) {
            return;
        }

        GraphicsObject graphicsObject = event.getGraphicsObject();
        if (graphicsObject.getId() == BLAST_ATTACK_SHADOW) {
            logManager.logNpcSkillshot(NpcID.MAD_ANGEL, "Blast attack projectile", graphicsObject.getLocation());
            return;
        }

        int tick = client.getTickCount();
        if (lastSwipeTick != tick && graphicsObject.getId() == SWIPE_ATTACK_SMOKE_ID) {
            logManager.logNpcAttack(NpcID.MAD_ANGEL, "Swipe attack");
            lastSwipeTick = tick;
            return;
        }

        if (lastExplosionTick != tick && graphicsObject.getId() == BLAST_ATTACK_EXPLOSION_ID) {
            logManager.logNpcAttack(NpcID.MAD_ANGEL, "Blast attack explosion");
            lastExplosionTick = tick;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (!logManager.isLogging()) {
            return;
        }

        Actor actor = event.getActor();
        if (!isMadAngel(actor)) {
            return;
        }

        NpcAnimation npcAnimation = NpcAnimation.getAnimation(actor.getAnimation());
        if (npcAnimation != null) {
            logManager.logNpcAttack(NpcID.MAD_ANGEL, npcAnimation.getName());
        }
    }

    private boolean isMadAngel(Actor actor) {
        if (!(actor instanceof NPC)) {
            return false;
        }

        int actorId = ((NPC) actor).getId();
        return actorId == NpcID.MAD_ANGEL;
    }

    private boolean isMadAngelDead(Actor actor) {
        if (!(actor instanceof NPC)) {
            return false;
        }

        int actorId = ((NPC) actor).getId();
        return actorId == NpcID.MAD_ANGEL_INITIAL || actorId == NpcID.MAD_ANGEL_DEAD;
    }
}