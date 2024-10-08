package com.pickpockethelper.entity;

import lombok.Getter;
import net.runelite.api.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Splasher {
    @Getter
    private Player player;
    @Getter
    private Instant lastAttack;

    private final List<Runnable> isAttackingListeners = new ArrayList<>();
    private final List<Runnable> playerListeners = new ArrayList<>();

    @Getter
    private Instant lastIdleNotify;

    public void updatePlayer(Player player) {
        this.player = player;
        playerListeners.forEach(Runnable::run);
    }

    public void addPlayerListener(Runnable runnable) {
        playerListeners.add(runnable);
    }

    public void updateLastIdleNotify() {
        lastIdleNotify = Instant.now();
    }

    public void updateLastAttack() {
        lastAttack = Instant.now();
        lastIdleNotify = null;
    }

    public void updateIsAttacking() {
        isAttackingListeners.forEach(Runnable::run);
    }

    public boolean isAttacking() {
        Duration timeSinceLastAtack = Duration.between(lastAttack, Instant.now());
        return timeSinceLastAtack.getSeconds() < 4;
    }

    public void addIsAttackingListener(Runnable runnable) {
        isAttackingListeners.add(runnable);
    }

    public void reset(){
        lastAttack = null;
        player = null;
        lastIdleNotify = null;
    }

    public void clear() {
        isAttackingListeners.clear();
        playerListeners.clear();

        reset();
    }

    @Override
    public String toString(){
        return player.getName();
    }
}
