package com.pickpockethelper.entity;

import com.pickpockethelper.utility.Helper;
import java.util.Objects;
import lombok.Getter;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Target {
    private static final int DEFAULT_DESPAWN_TIME = 300;
    private static final int ARDY_KNIGHT_DESPAWN_TIME = 600;

    private final Session session;

    @Getter
    private NPC npc;

    private final List<Runnable> npcListeners = new ArrayList<>();
    @Getter
    private WorldPoint lastLocation;

    @Getter
    private Instant lastMove;

    @Getter
    private Instant lastDespawnNotify;

    public Target(Session session) {
        this.session = session;
    }

    public void setNpc(NPC npc) {
        if(this.npc != null && session.isTarget(npc)) {
            return;
        }

		boolean similarTarget = this.npc != null && (this.npc.getIndex() == npc.getIndex() || Objects.equals(npc.getName(), this.npc.getName()));

        this.npc = npc;
        this.lastLocation = npc.getWorldLocation();

		if(!similarTarget) {
			session.reset();
		}

        npcListeners.forEach(Runnable::run);
    }

    public void updateLocation(WorldPoint location) {
        this.lastMove = Instant.now();
        this.lastLocation = location;
    }

    public void updateLastDespawnNotify() {
        lastDespawnNotify = Instant.now();
    }

    public void addNpcListener(Runnable runnable) {
        npcListeners.add(runnable);
    }

    public boolean isRendered() {
        return Helper.isRendered(npc);
    }

    public int getSecondsBeforeDespawn() {
        if (lastMove == null) {
            return -1;
        }
        int despawnTime = isArdyKnight() ? ARDY_KNIGHT_DESPAWN_TIME : DEFAULT_DESPAWN_TIME;
        int secondsSinceLastMove = Helper.secondsSince(lastMove);
        Integer secondsSinceLastAttackReceived = (session.getSplasher().getLastAttack() != null) ? Helper.secondsSince(session.getSplasher().getLastAttack()) : null;
        int secondsBeforeDespawn = (secondsSinceLastAttackReceived == null || secondsSinceLastMove < secondsSinceLastAttackReceived) ? despawnTime - secondsSinceLastMove : despawnTime - secondsSinceLastAttackReceived;

        if (secondsBeforeDespawn == despawnTime) {
            lastDespawnNotify = null;
        }

        return secondsBeforeDespawn;
    }

    public void reset() {
        npc = null;
        lastLocation = null;
        lastMove = null;
        lastDespawnNotify = null;
    }

    public void clear() {
        npcListeners.clear();
        reset();
    }

    private boolean isArdyKnight() {
        return npc != null && "Knight of Ardougne".equals(npc.getName());
    }
}
