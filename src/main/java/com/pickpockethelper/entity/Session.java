package com.pickpockethelper.entity;

import lombok.Getter;
import net.runelite.api.Actor;

import javax.inject.Singleton;
import java.time.Duration;
import java.time.Instant;

/**
 * The currently ongoing streak of pickpocketing a specific target.
 * Keeps track of the pickpocket target, potential splashers, and relevant events and stats.
 */
@Getter
@Singleton
public class Session {
    private final Target target;
    private final Splasher splasher;
    private Instant lastPickpocketAttempt;
    private Instant lastPickpocketSuccess;
    private Instant lastPlayerIdleNotify;
    private Instant lastRogueEquipmentProc;
    private Instant lastStun;
    private int pickpocketSuccessCount = 0;
    private int pickpocketFailCount = 0;
    private Instant sessionStart;

    public Session() {
        target = new Target(this);
        splasher = new Splasher();
    }

    public void updateLastPickpocketAttempt(){
        this.lastPickpocketAttempt = Instant.now();
    }

    public void updateLastPlayerIdleNotify() {
        this.lastPlayerIdleNotify = Instant.now();
    }

    public void updateLastPickpocketSuccess() {
        this.lastPickpocketSuccess = Instant.now();
    }

    public void updateLastRogueEquipmentProc() {
        this.lastRogueEquipmentProc = Instant.now();
    }

    public void updateLastStun(){
        this.lastStun = Instant.now();
    }

    public void increasePickpocketSuccessCount(){
        pickpocketSuccessCount++;
    }

    public void increasePickpocketFailCount(){
        pickpocketFailCount++;
    }

    public void setupListeners() {
        target.addNpcListener(this::onTargetChange);
    }

    private void onTargetChange() {
        if (!isActive()) {
            sessionStart = Instant.now();
        }
    }

    /**
     * Determine if the local player is currently pickpocketing or is stunned after a failed pickpocket.
     *
     * @param delay a duration to expand the pickpocketing timeframe.
     */
    public boolean isPickpocketing(Duration delay) {
        if (lastPickpocketAttempt == null) {
            return false;
        }

        if (lastStun != null) {
            Duration stunDuration = Duration.between(lastStun, Instant.now());
            if (stunDuration.getSeconds() < 6) {
                delay = delay.plus(Duration.ofSeconds(6));
            }
        }

        return lastPickpocketAttempt.plus(delay).isAfter(Instant.now());
    }

	/**
	 * Determine if the current session is still active.
	 * A session deactivates after 2 minutes of no pickpocketing.
	 */
	public boolean isActive() {
		return this.isPickpocketing(Duration.ofSeconds(120));
	}

    /**
     * Determine if the given actor is the player's target.
     *
     * @param actor the actor being checked.
     */
    public boolean isTarget(Actor actor) {
        return actor != null && actor == target.getNpc();
    }

    public void reset() {
        lastPickpocketAttempt = null;
        lastPickpocketSuccess = null;
        lastRogueEquipmentProc = null;
        lastPlayerIdleNotify = null;
        lastStun = null;
        pickpocketSuccessCount = 0;
        pickpocketFailCount = 0;
        sessionStart = null;
    }

    public void clear() {
        splasher.clear();
        target.clear();
        reset();
    }
}
