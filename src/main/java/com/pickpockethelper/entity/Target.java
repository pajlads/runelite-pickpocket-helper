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
        if(lastMove == null) {
            return -1;
        }
        int secondsSinceLastMove = Helper.secondsSince(lastMove);
        Integer secondsSinceLastAttackReceived = (session.getSplasher().getLastAttack() != null) ? Helper.secondsSince(session.getSplasher().getLastAttack()) : null;
        int secondsBeforeDespawn = (secondsSinceLastAttackReceived == null || secondsSinceLastMove < secondsSinceLastAttackReceived) ? 300 - secondsSinceLastMove : 300 - secondsSinceLastAttackReceived;

        if (secondsBeforeDespawn == 300) {
            lastDespawnNotify = null;
        }

        return secondsBeforeDespawn;
    }

    public void clear() {
        npcListeners.clear();

        npc = null;
        lastLocation = null;
        lastMove = null;
        lastDespawnNotify = null;
    }
}
