package com.rslogs.util;

import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.gameval.NpcID;

public final class NpcUtils {
    public static boolean isNpc(Actor actor, int npcId) {
        if (!(actor instanceof NPC)) {
            return false;
        }

        int actorId = ((NPC) actor).getId();
        return actorId == npcId;
    }
}
