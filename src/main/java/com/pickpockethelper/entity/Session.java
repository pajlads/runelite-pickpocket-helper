package com.pickpockethelper.entity;

import lombok.Getter;
import net.runelite.api.Actor;
import net.runelite.api.NPC;

import javax.inject.Singleton;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * The currently ongoing streak of pickpocketing a specific target.
 * Keeps track of the pickpocket target, potential splashers, and relevant events and stats.
 */
@Singleton
public class Session {
    @Getter
    private final Target target;
    @Getter
    private final Splasher splasher;
    @Getter
    private Instant lastPickpocketAttempt;
    private final List<Runnable> lastPickpocketAttemptListeners = new ArrayList<>();
    @Getter
    private Instant lastPickpocketSuccess;
    @Getter
    private Instant lastPlayerIdleNotify;
    @Getter
    private Instant lastRogueEquipmentProc;
    @Getter
    private Instant lastStun;
    @Getter
    private Integer pickpocketSuccessCount = 0;
    @Getter
    private Integer pickpocketFailCount = 0;
    @Getter
    private Instant sessionStart;

    public Session() {
        target = new Target(this);
        splasher = new Splasher();
    }

    /**
     * Resets
     */
    public void updateLastPickpocketAttempt(){
        this.lastPickpocketAttempt = Instant.now();
        lastPickpocketAttemptListeners.forEach(Runnable::run);
    }

    public void addLastPickpocketAttemptListener(Runnable runnable) {
        lastPickpocketAttemptListeners.add(runnable);
    }

    public void updateLastPlayerIdleNotify() { this.lastPlayerIdleNotify = Instant.now();}
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
        reset();
        sessionStart = Instant.now();
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
     * Determine if the given actor is the player's target.
     *
     * @param actor the actor being checked.
     */
    public boolean isTarget(Actor actor) {
        if(target.getNpc() == null || !(actor instanceof NPC)) {
            return false;
        }

        return target.getNpc().equals(actor);
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
        lastPickpocketAttemptListeners.clear();
        splasher.clear();
        target.clear();

        reset();
    }
}
